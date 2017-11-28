package com.randioo.compare_collections_server.module.fight.service;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.protobuf.Message;
import com.randioo.compare_collections_server.GlobleConstant;
import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.FightConstant;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.*;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.fight.component.parser.ScoreProtoParser;
import com.randioo.compare_collections_server.module.fight.component.processor.Processor;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.ISafeCheckCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.*;
import com.randioo.compare_collections_server.protocol.Error.ErrorCode;
import com.randioo.compare_collections_server.protocol.Fight.*;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.quartz.QuartzManager;
import com.randioo.randioo_server_base.annotation.BaseServiceAnnotation;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@BaseServiceAnnotation("fightService")
@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {
    @Autowired
    MatchService matchService;

    @Autowired
    public Processor processor;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private SeatManager seatManager;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private ScoreProtoParser scoreProtoParser;

    @Autowired
    private AudienceManager audienceManager;

    @Autowired
    private QuartzManager quartzManager;

    @Override
    public void initService() {
    }

    @Override
    public void ready(Role role) {
        role.logger.info("readyGame");
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            FightReadyResponse response = FightReadyResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightReadyResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }
        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            return;
        }
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        // 游戏准备
        // 返回本玩家收到该消息
        SessionUtils.sc(
                roleGameInfo.roleId,
                SC.newBuilder()
                        .setFightReadyResponse(FightReadyResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                        .build());

        eventBus.post(new EventReadyResponse(game, gameRoleId));

        synchronized (game) {
            // 游戏准备
            roleGameInfo.ready = true;
            game.readySet.add(roleGameInfo.seat);
            SC scFightReady = SC.newBuilder()
                    .setSCFightReady(SCFightReady.newBuilder().setSeat(roleGameInfo.seat))
                    .build();

            // 通知其他所有玩家，该玩家准备完毕
            gameBroadcast.broadcast(game, scFightReady);
            EventReady eventReady = new EventReady(game, scFightReady, roleGameInfo.gameRoleId);
            eventBus.post(eventReady);

            notifyObservers(FightConstant.FIGHT_READY, scFightReady, game);
            // boolean matchAI =
            // game.envVars.Boolean(GlobleConstant.ARGS_MATCH_AI);
            // if (matchAI) {
            // matchService.fillAI(game);
            // }
            processor.nextProcess(game, "role_game_ready");
        }
    }

    @Override
    public boolean checkAllReady(Game game) {
        game.logger.info("checkAllReady");
        GameConfigData gameConfigData = game.getGameConfig();
        // 最少两个人
        if (game.getRoleIdMap().size() < gameConfigData.getMinCount()) {
            return false;
        }

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            if (!info.ready) {
                // 如果是第一把还没开始并且是房主，不用检测
                if (game.getFinishRoundCount() == 0 && info.roleId == game.getMasterRoleId()) {
                    continue;
                }
                return false;
            }
        }
        return true;
    }

    /**
     * 开始游戏
     *
     * @param role
     */
    @Override
    public void gameStart(Role role) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightGameStartResponse(
                                    FightGameStartResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        if (game.getRoleIdMap().size() < game.getGameConfig().getMinCount()) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightGameStartResponse(
                                    FightGameStartResponse.newBuilder().setErrorCode(
                                            ErrorCode.ROLE_NOT_ENOUGH.getNumber()))
                            .build());
            return;
        }
        boolean allReady = checkAllReady(game);

        if (allReady) {
            boolean matchAI = game.envVars.Boolean(GlobleConstant.ARGS_MATCH_AI);
            if (matchAI) {
                matchService.fillAI(game);
            }
        }
        // 返回开始游戏
        FightGameStartResponse response = FightGameStartResponse.newBuilder()
                .setErrorCode(allReady ? ErrorCode.OK.getNumber() : ErrorCode.NOT_ALL_READY.getNumber())
                .build();
        SC matchStartGameResponse = SC.newBuilder().setFightGameStartResponse(response).build();
        SessionUtils.sc(role.getRoleId(), matchStartGameResponse);
        synchronized (game) {
            if (allReady) {// 有人没准备不能进入流程
                // 加入游戏流程中去
                processor.nextProcess(game, "role_game_start");
            }
        }
    }

    @Override
    public void bet(int score, Role role) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            FightBetResponse response = FightBetResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightBetResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }
        if (!GlobleClass._G.sdb.bet_list.contains(score)) {// 叫的分数不对
            FightBetResponse response = FightBetResponse.newBuilder()
                    .setErrorCode(ErrorCode.CALL_SCORE_ERROR.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightBetResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

        FightBetResponse response = FightBetResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).build();
        SC scResponse = SC.newBuilder().setFightBetResponse(response).build();
        SessionUtils.sc(roleGameInfo.roleId, scResponse);

        bet(roleGameInfo, game, score);
    }

    @Override
    public void bet(RoleGameInfo roleGameInfo, Game game, int score) {
        synchronized (game) {
          //  quartzManager.cancelJob(roleGameInfo.gameRoleId);
            roleGameInfo.isCalled = true;
            roleGameInfo.betScore = score;
            // 通知其他玩家该玩家叫的分
            int currentSeat = game.getCurrentSeat();
            SCFightBetScore scFightCallScore = SCFightBetScore.newBuilder().setSeat(currentSeat).setScore(score).build();
            SC scCallScore = SC.newBuilder().setSCFightBetScore(scFightCallScore).build();
            gameBroadcast.broadcast(game, scCallScore);

            processor.nextProcess(game, "role_bet");
        }
    }

    @Override
    public void continueAddCard(Role role, boolean addCard) {
        int gameId = role.getGameId();
        Game game = gameManager.get(gameId);
        if (game == null) {
            FightChooseAddCardResponse response = FightChooseAddCardResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightChooseAddCardResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }
        SessionUtils.sc(role.getRoleId(), SC.newBuilder()
                .setFightChooseAddCardResponse(FightChooseAddCardResponse.newBuilder()
                        .setErrorCode(ErrorCode.OK.getNumber())).build());

        RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());

        coreContinueAddCard(game, roleGameInfo, addCard);
    }

    public void coreContinueAddCard(Game game, RoleGameInfo roleGameInfo, boolean addCard) {
        synchronized (game) {
           // quartzManager.cancelJob(roleGameInfo.gameRoleId);
            roleGameInfo.needCard = addCard;
            processor.nextProcess(game, "role_choose_add_card");
        }
    }

    /**
     * 看牌
     *
     * @param role
     * @return
     */
    @Override
    public void watchCards(Role role) {
        role.logger.info("watchCards");
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightLookPaiResponse response = FightLookPaiResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightLookPaiResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            if (!game.getRule().getSafeCheckCallType().isCallSafe(game, roleGameInfo)) {
                return;
            }
            if (game.getGameConfig().getOutLookCount() > game.loop) {// 没有达到了焖牌的最大轮数可以看牌了
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightLookPaiResponse(
                                        FightLookPaiResponse.newBuilder().setErrorCode(
                                                ErrorCode.NOT_WATCH_TURN.getNumber()))
                                .build());
                return;
            }
            FightLookPaiResponse response = FightLookPaiResponse.newBuilder()
                    .addAllCards(roleGameInfo.cards)
                    .setErrorCode(ErrorCode.OK.getNumber())
                    .build();
            SC responseSC = SC.newBuilder().setFightLookPaiResponse(response).build();
            SessionUtils.sc(role.getRoleId(), responseSC);
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.WATCH);// 看牌
        }
    }

    /**
     * 跟
     *
     * @param role
     */
    @Override
    public void follow(Role role, int betMoney) {
        role.logger.info("follow");
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightGenResponse response = FightGenResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightGenResponse(response).build());
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

            ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
            if (!safe.isCallSafe(game, roleGameInfo)) {
                game.logger.error("不是你的回合 玩家:{}", roleGameInfo.gameRoleId);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightGenResponse(
                                        FightGenResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }

            if (safe.checkFollow(roleGameInfo, game)) {
                logger.error("{}<={} 不能跟", roleGameInfo.chipMoney + roleGameInfo.betScore, game.maxChipMoney);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightGenResponse(
                                        FightGenResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightGenResponse(FightGenResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());
            if (game.getRule().getSafeCheckCallType().checkFollow(roleGameInfo, game)) {
                logger.error("{}<={} 不能跟", roleGameInfo.chipMoney + roleGameInfo.betScore, game.maxChipMoney);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightGenResponse(
                                        FightGenResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightGenResponse(FightGenResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());

            eventBus.post(new EventFollowResponse(roleGameInfo.gameRoleId, game));

            logger.info("{}", game);
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.FOLLOW);
            logger.info("{}", game);
        }
    }

    /**
     * 大
     *
     * @param role
     * @param betMoney
     */
    @Override
    public void bigger(Role role, int bigMoney) {
        role.logger.info("bigger");
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightBiggerResponse response = FightBiggerResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightBiggerResponse(response).build());
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

            ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
            if (!safe.isCallSafe(game, roleGameInfo)) {
                game.logger.error("不是你的回合 玩家:{}", roleGameInfo.gameRoleId);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightBiggerResponse(
                                        FightBiggerResponse.newBuilder().setErrorCode(
                                                ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            // 赌注安全检查
            if (safe.checkBigger(roleGameInfo, game, bigMoney)) {
                game.logger.error("{}<={} 不能大", roleGameInfo.chipMoney, bigMoney);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightBiggerResponse(
                                        FightBiggerResponse.newBuilder().setErrorCode(
                                                ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightBiggerResponse(
                                    FightBiggerResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());

            eventBus.post(new EventBiggerResponse(roleGameInfo.gameRoleId, game));
            logger.info("{}", game);
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.BIGGER + " " + bigMoney);
            logger.info("{}", game);
        }
    }

    /**
     * 敲
     *
     * @param role
     */
    @Override
    public void betAll(Role role) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightBetAllResponse response = FightBetAllResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC scBetAll = SC.newBuilder().setFightBetAllResponse(response).build();
            SessionUtils.sc(role.getRoleId(), scBetAll);
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

            ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
            if (!safe.isCallSafe(game, roleGameInfo)) {
                game.logger.error("不是你的回合 玩家:{}", roleGameInfo.gameRoleId);
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightBetAllResponse(
                                        FightBetAllResponse.newBuilder().setErrorCode(
                                                ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightBetAllResponse(
                                    FightBetAllResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());

            eventBus.post(new EventBetAllResponse(roleGameInfo.gameRoleId, game));

            logger.info("{}", game);
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.BET_ALL);
            logger.info("{}", game);
        }
    }

    /**
     * 休
     *
     * @param role
     */
    @Override
    public void guo(Role role) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightGuoResponse response = FightGuoResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC scGuo = SC.newBuilder().setFightGuoResponse(response).build();
            SessionUtils.sc(role.getRoleId(), scGuo);
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

            ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
            if (!safe.isCallSafe(game, roleGameInfo)) {
                FightGuoResponse response = FightGuoResponse.newBuilder()
                        .setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())
                        .build();
                SC scGuo = SC.newBuilder().setFightGuoResponse(response).build();
                SessionUtils.sc(roleGameInfo.roleId, scGuo);
                return;
            }
            if (safe.checkGuo(roleGameInfo, game)) {
                logger.error("有前一个下注的人,不能休");
                FightGuoResponse response = FightGuoResponse.newBuilder()
                        .setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber())
                        .build();
                SC scGuo = SC.newBuilder().setFightGuoResponse(response).build();
                SessionUtils.sc(roleGameInfo.roleId, scGuo);
                return;
            }
            FightGuoResponse response = FightGuoResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).build();
            SC scGuo = SC.newBuilder().setFightGuoResponse(response).build();
            SessionUtils.sc(roleGameInfo.roleId, scGuo);

            eventBus.post(new EventGuoResponse(roleGameInfo.gameRoleId, game));

            logger.info(game + "");
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.GUO);
            logger.info(game + "");
        }
    }

    /**
     * 放弃
     *
     * @param role
     */
    @Override
    public void giveUp(Role role) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightGiveUpResponse response = FightGiveUpResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightGiveUpResponse(response).build());
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
            if (!safe.isCallSafe(game, roleGameInfo)) {
                SessionUtils.sc(
                        roleGameInfo.roleId,
                        SC.newBuilder()
                                .setFightGiveUpResponse(
                                        FightGiveUpResponse.newBuilder().setErrorCode(
                                                ErrorCode.NOT_YOUR_TURN.getNumber()))
                                .build());
                return;
            }
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightGiveUpResponse(
                                    FightGiveUpResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());

            eventBus.post(new EventGiveUpResponse(roleGameInfo.gameRoleId, game));

            logger.info("{}", game);
            processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.GIVE_UP);
            logger.info("{}", game);
        }
    }

    /**
     * 对决
     *
     * @param role
     * @param seat
     */
    @Override
    public void fight(Role role, int seat) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightTwoResponse response = FightTwoResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SC scResponse = SC.newBuilder().setFightTwoResponse(response).build();
            SessionUtils.sc(role.getRoleId(), scResponse);
            return;
        }
        int currentSeat = seatManager.getSeatByRoleId(game, role.getRoleId());
        RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, currentSeat);
        ISafeCheckCallType safe = game.getRule().getSafeCheckCallType();
        if (!safe.isCallSafe(game, roleGameInfo)) {
            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightGiveUpResponse(
                                    FightGiveUpResponse.newBuilder().setErrorCode(ErrorCode.NOT_YOUR_TURN.getNumber()))
                            .build());
            return;
        }
        SessionUtils.sc(
                roleGameInfo.roleId,
                SC.newBuilder()
                        .setFightTwoResponse(FightTwoResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                        .build());
        processor.nextProcess(game, "flow_choose_call_type " + CallTypeEnum.BATTLE + " " + seat);
    }


    /**
     * 分牌
     *
     * @param role
     */
    @Override
    public void cutCards(Role role, List<Integer> cards) {

        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            FightCutCardsResponse response = FightCutCardsResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightCutCardsResponse(response).build());
            return;
        }
        synchronized (game) {
            int seat = seatManager.getSeatByRoleId(game, role.getRoleId());
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

            roleGameInfo.cards.clear();
            roleGameInfo.cards.addAll(cards);

            SessionUtils.sc(
                    roleGameInfo.roleId,
                    SC.newBuilder()
                            .setFightCutCardsResponse(
                                    FightCutCardsResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                            .build());

            processor.nextProcess(game, "role_cut_cards " + roleGameInfo.seat);

            eventBus.post(new EventCutCardsResponse(roleGameInfo.gameRoleId, game));
        }
    }

    @Override
    public void getGameSerialize(Role role) {
        Game game = gameManager.get(role.getRoleId());

        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightGetRoomDataResponse(
                                    FightGetRoomDataResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        synchronized (game) {
            Gson gson = new Gson();
            String json = gson.toJson(game);
            role.logger.info("断线重连数据/n {}", json);
            SC sc = SC.newBuilder()
                    .setFightGetRoomDataResponse(
                            FightGetRoomDataResponse.newBuilder()
                                    .setErrorCode(ErrorCode.OK.getNumber())
                                    .setGameConfigData(game.getGameConfig())
                                    .setGameJson(json))
                    .build();
            SessionUtils.sc(role.getRoleId(), sc);
        }
    }

    @Override
    public void reconnect(Role role) {
        Game game = gameManager.get(role.getGameId());
        if (game == null) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setFightReconnectDataResponse(
                                    FightReconnectDataResponse.newBuilder().setErrorCode(
                                            ErrorCode.GAME_NOT_EXIST.getNumber()))
                            .build());
            return;
        }

        synchronized (game) {
            IReconnector<Game, Role, Message> reconnector = game.getRule().getReconnector(game);
            Message reconnectData = reconnector.getReconnectData(game, role);

            FightReconnectDataResponse.Builder builder = FightReconnectDataResponse.newBuilder().setErrorCode(
                    ErrorCode.OK.getNumber());

            if (reconnectData instanceof CxReconnectedData) {
                builder.setCxReconnectData((CxReconnectedData) reconnectData);
            } else if (reconnectData instanceof ZjhReconnectedData) {
                builder.setZjhReconnectedData((ZjhReconnectedData) reconnectData);
            } else if (reconnectData instanceof SdbReconnectedData) {
                builder.setSdbReconnectedData((SdbReconnectedData) reconnectData);
            }
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightReconnectDataResponse(builder).build());
        }

    }
}
