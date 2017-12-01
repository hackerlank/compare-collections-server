package com.randioo.compare_collections_server.module.match.service;

import com.google.common.eventbus.AsyncEventBus;
import com.google.protobuf.Message;
import com.randioo.compare_collections_server.GlobleConstant;
import com.randioo.compare_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.cache.local.MatchCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.file.GameRoundConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.entity.po.RoleMatchRule;
import com.randioo.compare_collections_server.module.ServiceConstant;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.GoldGameTypeManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.fight.component.parser.GameConfigProtoParser;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.processor.Processor;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.CxRule;
import com.randioo.compare_collections_server.module.fight.component.rule.tenhalf.TenHalfRule;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.ZJHRule;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.module.match.MatchConstant;
import com.randioo.compare_collections_server.module.match.component.MatchInfo;
import com.randioo.compare_collections_server.module.match.component.MatchSystem;
import com.randioo.compare_collections_server.module.role.service.RoleService;
import com.randioo.compare_collections_server.protocol.Entity.*;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData.Builder;
import com.randioo.compare_collections_server.protocol.Error.ErrorCode;
import com.randioo.compare_collections_server.protocol.Match.*;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.vote.OneRejectEndExceptApplyerStrategy;
import com.randioo.compare_collections_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_server_base.annotation.BaseServiceAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.db.IdClassCreator;
import com.randioo.randioo_server_base.log.LoggerProxy;
import com.randioo.randioo_server_base.module.key.Key;
import com.randioo.randioo_server_base.module.key.KeyStore;
import com.randioo.randioo_server_base.module.key.RoomKey;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Session;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@BaseServiceAnnotation("matchService")
@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

    private static Logger gameRootLogger = LoggerFactory.getLogger(Game.class);
    @Autowired
    private IdClassCreator idClassCreator;

    @Autowired
    private LoginService loginService;

    @Autowired
    private MatchModelService matchModelService;

    @Autowired
    private KeyStore keyStore;

    @Autowired
    private RoleService roleService;

    @Autowired
    private Processor processor;

    @Autowired
    private SeatManager seatManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameConfigProtoParser gameConfigProtoParser;

    @Autowired
    private GoldGameTypeManager goldGameTypeManager;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private AsyncEventBus asyncEventBus;

    @Autowired
    private MatchSystem matchSystem;

    @Autowired
    private AudienceManager audienceManager;

    @Override
    public void init() {
        // 初始化钥匙
        for (int i = 100000; i < 200000; i++) {
            Key key = new RoomKey();
            key.setValue(i);
            keyStore.putKey(key);
        }
        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_CHE_XUAN,
                (ICompareGameRule) SpringContext.getBean(CxRule.class));
        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_SHI_DIAN_BAN,
                (ICompareGameRule) SpringContext.getBean(TenHalfRule.class));
        GameCache.getRuleMap().put(ServiceConstant.COM_RANDIOO_ZHA_JIN_HUA,
                (ICompareGameRule) SpringContext.getBean(ZJHRule.class));

        idClassCreator.initId(Game.class, 0);

    }

    @Override
    public void initService() {

        matchModelService.setMatchHandler(new MatchHandler() {

            @Override
            public void outOfTime(MatchRule matchRule) {
                RoleMatchRule roleMatchRule = (RoleMatchRule) matchRule;
                int roleId = roleMatchRule.getRoleId();

                Role role = (Role) RoleCache.getRoleById(roleId);
                role.logger.info("匹配超时 {}", roleMatchRule.toString());
            }

            @Override
            public void matchSuccess(Map<String, MatchRule> matchMap) {
                List<RoleMatchRule> list = new ArrayList<>(matchMap.size());
                for (MatchRule matchRule : matchMap.values()) {
                    list.add((RoleMatchRule) matchRule);
                }

                Collections.sort(list);
                GameConfigData config = GameConfigData.newBuilder().build();
                Game game = createGame(list.get(0).getRoleId(), config);
                game.setGameType(GameType.GAME_TYPE_MATCH);

                for (MatchRule matchRule : matchMap.values()) {
                    RoleMatchRule rule = (RoleMatchRule) matchRule;

                    addAccountRole(game, rule.getRoleId());
                }

            }

            @Override
            public boolean checkMatchRule(MatchRule rule1, MatchRule rule2) {
                RoleMatchRule roleRule1 = (RoleMatchRule) rule1;
                RoleMatchRule roleRule2 = (RoleMatchRule) rule2;

                return roleRule1.getMaxCount() == roleRule2.getMaxCount();
            }

            @Override
            public boolean checkArriveMaxCount(MatchRule rule, Map<String, MatchRule> matchRuleMap) {
                RoleMatchRule roleRule = (RoleMatchRule) rule;

                return matchRuleMap.size() == roleRule.getMaxCount();
            }
        });
        matchModelService.initService();
    }

    @Override
    public void createRoom(Role role, GameConfigData gameConfigData) {
        // 检查配置是否可以创建游戏
        gameConfigData = addPropGameConfigData(gameConfigData);
        if (role.getGameId() != 0) {
            MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.IN_GAME.getNumber())
                    .build();
            SC sc = SC.newBuilder().setMatchCreateGameResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        if (!this.checkConfig(gameConfigData)) {
            MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.CREATE_FAILED.getNumber())
                    .build();
            SC sc = SC.newBuilder().setMatchCreateGameResponse(response).build();
            SessionUtils.sc(role.getRoleId(), sc);
            return;
        }
        int roundCount = gameConfigData.getRoundCount();
        GameRoundConfig config = GameRoundConfigCache.getGameRoundByRoundCount(roundCount);
        // 重读平台用户信息
        roleService.initRoleDataFromHttp(role);
        // if (role.getRandiooMoney() < config.needMoney) {
        // MatchCreateGameResponse response =
        // MatchCreateGameResponse.newBuilder()
        // .setErrorCode(ErrorCode.NOT_RANDIOOMONEY_ENOUGH.getNumber()).setNeedMoney(config.needMoney).build();
        // SC sc = SC.newBuilder().setMatchCreateGameResponse(response).build();
        // SessionUtils.sc(role.getRoleId(), sc);
        // return;
        // }
        // 创建游戏1
        Game game = this.createGame(role.getRoleId(), gameConfigData);
        // 标记房间为好友房间
        role.logger.info("创建房间 {} 房间ID {} 房间号 {} ,游戏类型: 好友房", gameConfigData, game.getGameId(), game.getGameConfig()
                .getRoomId());
        // 获得该玩家的id
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
        // 获得该玩家的游戏数据
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        // 游戏数据转协议游戏数据
        GameRoleData myGameRoleData = this.parseGameRoleData(roleGameInfo, game);
        // 创建游戏消息返回
        MatchCreateGameResponse response = MatchCreateGameResponse.newBuilder()
                .setErrorCode(ErrorCode.OK.getNumber())
                .setGameConfigData(game.getGameConfig())
                .setRoomId(game.getGameConfig().getRoomId())
                .setGameRoleData(myGameRoleData)
                .build();
        SC matchCreateGameResponse = SC.newBuilder().setMatchCreateGameResponse(response).build();
        SessionUtils.sc(role.getRoleId(), matchCreateGameResponse);
        // 发送创建游戏的通知
        this.notifyObservers(MatchConstant.MATCH_CREATE_GAME, game, roleGameInfo);
        processor.push(game, "wait");
        processor.process(game);
    }

    private boolean checkConfig(GameConfigData gameConfigData) {
        boolean check = true;
        check &= gameConfigData.getMaxCount() >= 2;// 检查人数大于2
        return check;
    }

    /**
     * 为配置表增加属性
     *
     * @param config
     * @return
     */
    private GameConfigData addPropGameConfigData(GameConfigData config) {
        config = config.toBuilder().setMaxCount(6).build();
        config = config.toBuilder().setMinCount(2).build();
        return config;
    }

    /**
     * 创建游戏
     *
     * @param roleId
     * @return
     */
    @Override
    public Game createGame(int roleId, GameConfigData gameConfigData) {
        // 通过配置表创建游戏
        Game game = this.createGameByGameConfig(gameConfigData, GameType.GAME_TYPE_FRIEND);
        // 设置房主
        game.setMasterRoleId(roleId);
        // 将创建房间的人加入到该房间
        this.addAccountRole(game, roleId);
        return game;
    }

    @Override
    public Game createGameByGameConfig(GameConfigData gameConfigData, GameType gameType) {
        Game game = new Game();
        int gameId = idClassCreator.getId(Game.class);
        game.setGameId(gameId);
        game.setGameType(gameType);
        game.setGameState(GameState.GAME_STATE_PREPARE);
        game.setFinishRoundCount(0);
        // 获得钥匙
        RoomKey key = (RoomKey) this.getLockKey();
        key.setGameId(gameId);
        game.setLockKey(key);
        String lockString = key.getName();
        Logger logger = LoggerProxy.proxyByName(gameRootLogger,
                MessageFormat.format("[roomId:{0},time:{1}]", lockString, TimeUtils.getDetailTimeStr()));
        game.logger = logger;
        // 设置房间号
        gameConfigData = gameConfigData.toBuilder().setRoomId(lockString).build();
        game.setGameConfig(gameConfigData);
        game.getVoteBox().setStrategy(new OneRejectEndExceptApplyerStrategy() {
            @Override
            public VoteResult waitVote(String joiner) {
                int roleId = Integer.parseInt(joiner.split("_")[1]);
                Object session = SessionCache.getSessionById(roleId);
                if (session == null || !Session.isConnected(session)) {
                    return VoteResult.PASS;
                }
                return VoteResult.WAIT;
            }
        });
        // 设置规则
        String gameString = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);
        ICompareGameRule<Game> rule = GameCache.getRuleMap().get(gameString);
        game.setRule(rule);
        game.getRule().initDataStructure(game);
        // 复制环境变量
        this.copyGlobleMap(game);
        GameCache.getGameMap().put(gameId, game);
        GameCache.getGameLockStringMap().put(lockString, gameId);
        if (gameType == GameType.GAME_TYPE_GOLD) {
            GameCache.getGoldModeGameIdList().add(gameId);
        }
        return game;
    }

    /**
     * 复制环境变量
     *
     * @param game
     * @author wcy 2017年9月4日
     */
    private void copyGlobleMap(Game game) {
        Field field = ReflectionUtils.findField(GlobleMap.class, "paramMap");
        ReflectionUtils.makeAccessible(field);
        @SuppressWarnings("unchecked")
        Map<String, Object> paramMap = (Map<String, Object>) ReflectionUtils.getField(field, null);
        game.envVars.putParam(paramMap);
    }

    /**
     * 加入玩家
     *
     * @param game
     * @param roleId
     * @author wcy 2017年5月26日
     */
    @Override
    public void addAccountRole(Game game, int roleId) {
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, roleId);
        game.logger.debug("加入玩家 {}", gameRoleId);
        this.addRole(game, roleId, gameRoleId);
    }

    /**
     * 加入ai
     *
     * @param game
     * @author wcy 2017年5月26日
     */
    private String addAIRole(Game game) {
        String gameRoleId = roleGameInfoManager.getAIGameRoleId(game.getGameId());
        addRole(game, 0, gameRoleId);
        // 机器人自动准备完毕
        game.getRoleIdMap().get(gameRoleId).ready = true;
        return gameRoleId;
    }

    /**
     * 添加玩家的接口,无论是否是人工智能
     *
     * @param game
     * @param roleId
     * @param gameRoleId
     */
    private void addRole(Game game, int roleId, String gameRoleId) {
        // 创建玩家游戏数据
        if (game.getRoleIdMap().containsKey(gameRoleId)) {
            return;
        }
        String lockString = game.getGameConfig().getRoomId();
        RoleGameInfo roleGameInfo = this.createRoleGameInfo(roleId, gameRoleId, game);
        game.logger.debug("加入玩家roleId={}", roleId);
        logger.info("房间{}加入:{} ", lockString, roleId);

        game.getRoleIdMap().put(gameRoleId, roleGameInfo);

        roleGameInfo.seat = game.getRoleIdList().size();
        game.getRoleIdList().add(gameRoleId);

        game.logger.info("加入前 {}", game.getSeatMap());
        game.getSeatMap().put(roleGameInfo.seat, roleGameInfo);
        game.logger.info("加入后 {}", game.getSeatMap());

        if (roleId != 0) {
            Role role = loginService.getRoleById(roleId);
            role.setGameId(game.getGameId());
            role.logger.info("加入房间{}", lockString);
        }
    }

    /**
     * 创建用户在游戏中的数据结构
     *
     * @param roleId
     * @param gameRoleId
     * @param game
     * @return
     * @author wcy 2017年5月25日
     */
    private RoleGameInfo createRoleGameInfo(int roleId, String gameRoleId, Game game) {
        boolean isMaster = roleId == game.getMasterRoleId();
        RoleGameInfo roleGameInfo = new RoleGameInfo();
        roleGameInfo.roleId = roleId;
        roleGameInfo.gameRoleId = gameRoleId;
        roleGameInfo.seat = -1;
        // 是房主默认准备
        roleGameInfo.ready = isMaster;
        roleGameInfo.isMaster = isMaster;
        return roleGameInfo;
    }

    /**
     * 检查到达房间最大人数
     *
     * @param game
     * @return
     * @author wcy 2017年7月28日
     */
    private boolean checkRoomMaxCount(Game game) {
        GameConfigData config = game.getGameConfig();
        int currentCount = game.getRoleIdMap().size() + audienceManager.getAudiences(game.getGameId()).size();
        return currentCount >= config.getMaxCount();
    }

    @Override
    public void clearSeatByGameRoleId(Game game, String gameRoleId) {
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        game.getSeatMap().remove(roleGameInfo.seat);
        roleGameInfo.seat = -1;
    }

    @Override
    public void fillAI(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();
        // 检查是否可以加入npc
        int needAllAICount = gameConfigData.getMaxCount() - game.getRoleIdMap().size();
        // 先检查要发送给多少个真人
        List<RoleGameInfo> realRoleGameInfos = new ArrayList<>(game.getRoleIdMap().values());
        for (int i = 0; i < needAllAICount; i++) {
            String aiGameRoleId = addAIRole(game);

            RoleGameInfo info = game.getRoleIdMap().get(aiGameRoleId);
            GameRoleData aiGameRoleData = this.parseGameRoleData(info, game);

            SC scJoinGame = SC.newBuilder()
                    .setSCMatchJoinGame(SCMatchJoinGame.newBuilder().setGameRoleData(aiGameRoleData))
                    .build();
            for (RoleGameInfo roleGameInfo : realRoleGameInfos) {
                SessionUtils.sc(roleGameInfo.roleId, scJoinGame);
                this.notifyObservers(MatchConstant.JOIN_GAME, scJoinGame, game.getGameId(), info);
            }
        }
    }

    @Override
    public void match(Role role, int matchParameter) {
        int needGold = GlobleClass._G.need_gold;
        if (goldGameTypeManager.isGoldNotEnough(role, needGold)) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setMatchResponse(MatchResponse.newBuilder().setErrorCode(ErrorCode.NO_GOLD.getNumber()))
                            .build());
            return;
        }
        MatchInfo matchInfo = new MatchInfo(role.getRoleId(), matchParameter);
        if (MatchCache.getWaitQueue().contains(matchInfo)) {
            SessionUtils.sc(
                    role.getRoleId(),
                    SC.newBuilder()
                            .setMatchResponse(MatchResponse.newBuilder().setErrorCode(ErrorCode.ERROR.getNumber()))
                            .build());
            return;
        }

        SessionUtils.sc(role.getRoleId(),
                SC.newBuilder()
                        .setMatchResponse(MatchResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                        .build());

        matchSystem.match(role, matchInfo);
    }

    @Override
    public void cancelMatch(Role role) {
        serviceCancelMatch(role);
        SessionUtils.sc(
                role.getRoleId(),
                SC.newBuilder()
                        .setMatchCancelResponse(MatchCancelResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                        .build());
    }

    @Override
    public void serviceCancelMatch(Role role) {
        matchSystem.cancel(role);
    }

    @Override
    public GameRoleData parseGameRoleData(RoleGameInfo info, Game game) {
        // FIXME
        int index = info.seat;
        boolean ready = info.ready;
        if (game.getGameState() == GameState.GAME_STATE_START) {
            ready = false;
        }

        // 如果是机器人，则都是上线状态
        if (info.roleId <= 0) {
            GameRoleData aiGameRoleData = GameRoleData.newBuilder()
                    .setGameRoleId(info.gameRoleId)
                    .setReady(ready)
                    .setAudience(false)
                    .setSeat(index)
                    .setName(ServiceConstant.ROBOT_PREFIX_NAME + info.gameRoleId)
                    .setOnline(true)
                    .build();
            return aiGameRoleData;
        }
        Role role = loginService.getRoleById(info.roleId);
        // 设置房主
        role.setIsMaster(game.getMasterRoleId() == info.roleId);

        Object session = SessionCache.getSessionById(info.roleId);
        boolean online = session != null && Session.isConnected(session);

        // 设置玩家平台id，就是帐号，如果是机器人使用默认字符串
        String platformRoleId = role == null ? ServiceConstant.ROBOT_PLATFORM_ID : role.getAccount();

        Builder builder = GameRoleData.newBuilder()
                .setGameRoleId(info.gameRoleId)
                .setReady(ready)
                .setSeat(index)
                .setAudience(false)
                .setName(role.getName())
                .setHeadImgUrl(StringUtils.handleMaybeNullString(role.getHeadImgUrl()))
                .setMoney(role.getRandiooMoney())
                .setSex(role.getSex())
                .setPoint(role.getPoint())
                .setOnline(online)
                .setPlatformRoleId(platformRoleId)
                .setMaster(role.getIsMaster())
                .setGold(role.getGold());

        return builder.build();
    }

    private Key getLockKey() {
        Key key = keyStore.getRandomKey();
        return key;
    }

    @Override
    public String getLockString(Key key) {
        return key.getValue() + "";
    }

    @Override
    public void checkRoom(String roomId, Object session) {
        Role role = RoleCache.getRoleBySession(session);

        Integer gameId = GameCache.getGameLockStringMap().get(roomId);
        if (gameId == null) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }

        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }

        boolean reachRoomMaxCount = checkRoomMaxCount(game);
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
        boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
        if (reachRoomMaxCount && !inRoom) {
            SessionUtils.sc(session,
                    SC.newBuilder()
                            .setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(false))
                            .build());
            return;
        }
        SessionUtils.sc(session,
                SC.newBuilder().setMatchCheckRoomResponse(MatchCheckRoomResponse.newBuilder().setExist(true)).build());

    }

    /**
     * 加入房间
     *
     * @param role
     * @param lockString
     * @return
     */
    @Override
    public void joinInRoom(Role role, String lockString) {// lockString==roomId
        MatchJoinInGameResponse.Builder responseBuilder = MatchJoinInGameResponse.newBuilder();
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        if (role.getGameId() != 0) {
            MatchJoinInGameResponse response = MatchJoinInGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.IN_GAME.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinInGameResponse(response).build());
            return;
        }
        if (gameId == null) {
            MatchJoinInGameResponse response = MatchJoinInGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinInGameResponse(response).build());
            return;
        }
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            MatchJoinInGameResponse response = MatchJoinInGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinInGameResponse(response).build());
            return;
        }

        // 当前游戏状态不允许加入
        if (game.getGameState() != GameState.GAME_STATE_PREPARE) {
            MatchJoinInGameResponse response = MatchJoinInGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.GAME_JOIN_ERROR.getNumber())
                    .build();
            SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinInGameResponse(response).build());
            return;
        }
        synchronized (game) {
            // 检查到达房间最大人数
            String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
            boolean reachMaxRoleCount = this.checkRoomMaxCount(game);
            boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
            // 房间到达上限且该人不在房间中
            if (reachMaxRoleCount && !inRoom) {
                responseBuilder.setErrorCode(ErrorCode.MATCH_MAX_ROLE_COUNT.getNumber());
                SessionUtils.sc(role.getRoleId(), SC.newBuilder().setMatchJoinInGameResponse(responseBuilder).build());
                return;
            }
            this.addAccountRole(game, role.getRoleId());
            // 游戏数据转协议游戏数据
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            List<GameRoleData> datas = new ArrayList<>();
            for (RoleGameInfo r : game.getRoleIdMap().values()) {
                datas.add(this.parseGameRoleData(r, game));
            }
            GameRoleData gameRoleData = this.parseGameRoleData(roleGameInfo, game);
            // 返回
            MatchJoinInGameResponse response = MatchJoinInGameResponse.newBuilder()
                    .setErrorCode(ErrorCode.OK.getNumber())
                    .setRoomId(lockString)
                    .setMyIndex(roleGameInfo.seat)
                    .addAllGameRoleData(datas)
                    .setGameConfigData(game.getGameConfig())
                    .build();
            SC matchJoinInGameResponse = SC.newBuilder().setMatchJoinInGameResponse(response).build();
            SessionUtils.sc(roleGameInfo.roleId, matchJoinInGameResponse);
            SC scMatchJoinIn = SC.newBuilder()
                    .setSCMatchJoinInGame(SCMatchJoinInGame.newBuilder().setGameRoleData(gameRoleData))
                    .build();
            gameBroadcast.broadcastBesides(game, scMatchJoinIn, roleGameInfo.roleId);
        }
    }

    @Override
    public void audienceJoinGame(Role role, String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        Game game = GameCache.getGameMap().get(gameId);
        synchronized (game) {
            // 检查到达房间最大人数
            String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
            boolean reachMaxRoleCount = this.checkRoomMaxCount(game);
            boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
            // 房间到达上限且该人不在房间中
            if (reachMaxRoleCount && !inRoom) {
                return;
            }
            // 加入观众列表
            audienceManager.add(gameId, role.getRoleId());
            role.setGameId(game.getGameId());
            // 我的虚拟座位号
            int mySeat = audienceManager.getSeat(role.getRoleId(), game);
            GameRoleData gameRoleData = this.parseAudienceGameRoleData(role, game, mySeat);

            SC SCMatchResult = this.getSCMatchResult(role, game);
            SessionUtils.sc(role.getRoleId(), SCMatchResult);

            // 通知其他玩家
            SC scMatchJoinIn = SC.newBuilder()
                    .setSCMatchJoinInGame(SCMatchJoinInGame.newBuilder().setGameRoleData(gameRoleData))
                    .build();
            gameBroadcast.broadcastBesides(game, scMatchJoinIn, role.getRoleId());
        }
    }

    private SC getSCMatchResult(Role role, Game game) {
        SCMatchResult.Builder builder = SCMatchResult.newBuilder();
        Message reconnectData = game.getRule().getReconnector(game).getReconnectData(game, role);
        if (reconnectData instanceof CxReconnectedData) {
            builder.setCxReconnectData((CxReconnectedData) reconnectData);
        } else if (reconnectData instanceof ZjhReconnectedData) {
            builder.setZjhReconnectedData((ZjhReconnectedData) reconnectData);
        } else if (reconnectData instanceof SdbReconnectedData) {
            builder.setSdbReconnectedData((SdbReconnectedData) reconnectData);
        }

        SC SCMatchResult = SC.newBuilder().setSCMatchResult(builder).build();
        return SCMatchResult;
    }

    @Override
    public GameRoleData parseAudienceGameRoleData(Role role, Game game, int seat) {
        Object session = SessionCache.getSessionById(role.getRoleId());
        boolean online = session != null && Session.isConnected(session);

        Builder builder = GameRoleData.newBuilder()
                .setReady(false)
                .setSeat(seat)
                .setName(role.getName())
                .setHeadImgUrl(StringUtils.handleMaybeNullString(role.getHeadImgUrl()))
                .setMoney(role.getRandiooMoney())
                .setSex(role.getSex())
                .setPoint(role.getPoint())
                .setOnline(online)
                .setAudience(true)
                .setPlatformRoleId(role.getAccount())
                .setMaster(role.getIsMaster());
        return builder.build();
    }

    /**
     * 匹配后加入游戏
     *
     * @param role
     * @param lockString
     */
    @Override
    public void matchJoinGame(Role role, String lockString) {
        Integer gameId = GameCache.getGameLockStringMap().get(lockString);
        if (gameId == null) {
            return;
        }
        Game game = GameCache.getGameMap().get(gameId);
        if (game == null) {
            return;
        }
        // 当前游戏状态不允许加入
        if (game.getGameState() == GameState.GAME_STATE_END) {
            return;
        }
        synchronized (game) {
            // 检查到达房间最大人数
            String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
            boolean reachMaxRoleCount = this.checkRoomMaxCount(game);
            boolean inRoom = game.getRoleIdMap().containsKey(gameRoleId);
            // 房间到达上限且该人不在房间中
            if (reachMaxRoleCount && !inRoom) {
                return;
            }
            this.addAccountRole(game, role.getRoleId());
            // 游戏数据转协议游戏数据
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

            GameRoleData gameRoleData = this.parseGameRoleData(roleGameInfo, game);
            // 返回
            SC SCMatchResult = getSCMatchResult(role, game);
            SessionUtils.sc(role.getRoleId(), SCMatchResult);
            // 通知其他玩家
            SC scMatchJoinIn = SC.newBuilder()
                    .setSCMatchJoinInGame(SCMatchJoinInGame.newBuilder().setGameRoleData(gameRoleData))
                    .build();
            gameBroadcast.broadcastBesides(game, scMatchJoinIn, roleGameInfo.roleId);

            // 如果达到开始的玩家数，则开始游戏
            if (game.getRoleIdMap().size() >= GlobleClass._G.min_count) {
                processor.push(game, "wait");
                processor.nextProcess(game, "role_game_start");
            }
        }
    }
}
