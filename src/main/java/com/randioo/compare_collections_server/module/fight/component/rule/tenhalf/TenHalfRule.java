/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.rule.tenhalf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.cache.file.TenHalfCardConfigCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.file.TenHalfCardConfig;
import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.FightConstant;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.round.tenhalf.TenHalfRoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.tenhalf.TenHalfRoundOverCalculator;
import com.randioo.compare_collections_server.module.fight.component.rule.CompareGameRuleAdapter;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.ISafeCheckCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.rule.tenhalf.calltype.TenHalfBet;
import com.randioo.compare_collections_server.module.fight.component.rule.tenhalf.calltype.TenHalfChooseAddCard;
import com.randioo.compare_collections_server.module.fight.component.rule.tenhalf.calltype.TenHalfGuo;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardTypeGetter;
import com.randioo.compare_collections_server.module.fight.component.zhuang.TenHalfZhuangCreator;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Entity;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData.Builder;

/**
 * @author zsy
 * @Description: 十点半规则
 * @date 2017年9月21日 上午11:45:28
 */
@Component
public class TenHalfRule extends CompareGameRuleAdapter<Game> {
    @Autowired
    private FightService fightService;

    @Autowired
    private SeatManager seatManager;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private CardTypeGetter cardTypeGetter;

    @Autowired
    private TenHalfRoundOverCalculator roundOverCalculator;

    @Autowired
    private TenHalfGuo tenHalfGuo;

    @Autowired
    private TenHalfChooseAddCard chooseAddCard;

    @Autowired
    private TenHalfBet bet;

    @Autowired
    private TenHalfZhuangCreator tenHalfZhuangCreator;

    @Autowired
    private TenHalfSafeCheckCallType tenHalfSafeCheckCallType;

    @Autowired
    private AudienceManager audienceManager;

    @Autowired
    private TenHalfReconnector tenHalfReconnector;

    @Autowired
    private VerifyManager verifyManager;

    private List<String> noticeBetProcess = Arrays.asList(//
            "FlowNoticeBet", //
            WAIT//
    );
    private List<String> nextAddCardProcess = Arrays.asList(//
            "FlowSeat 1", //
            "FlowNoticeChooseAddCard", //
            WAIT//
    );

    @Override
    public void initDataStructure(Game game) {

    }

    @Override
    public void setRoundOverInfo(Builder builder, RoundInfo roundInfo, Game game) {
        TenHalfRoundInfo tenHalfRoundInfo = (TenHalfRoundInfo) roundInfo;
        builder.setBetMoney(tenHalfRoundInfo.betMoney);
    }

    @Override
    public RoundOverCaculator getRoundResult(Game game) {
        return roundOverCalculator;
    }

    /**
     * 检查所有人都押过注没，庄家不用押注
     *
     * @param game
     * @return
     * @author wcy 2017年9月24日
     */
    private boolean checkAllBet(Game game) {
        int currentSeat = game.getCurrentSeat();
        int nextSeat = seatManager.getNext(game, currentSeat);
        if (nextSeat == game.getZhuangSeat()) {
            return true;
        }
        return false;
    }

    /**
     * 检测所有人都补过牌没
     *
     * @param game
     * @return
     */
    private boolean checkAllAddCard(Game game) {
        return game.getCurrentSeat() == game.getZhuangSeat();
    }

    @Override
    public List<Integer> getCards() {
        List<Integer> cards = new ArrayList<>();
        Map<Integer, TenHalfCardConfig> tenHalfCardMap = TenHalfCardConfigCache.getTenHalfCardMap();
        for (Integer i : tenHalfCardMap.keySet()) {
            cards.add(i);
        }
        return cards;
    }

    @Override
    public void checkCallTypes(Game game) {
    }

    @Override
    public List<String> afterCommandExecute(Game game, String flowName, String[] params) {
        List<String> list = new ArrayList<>();
        switch (flowName) {
        case "role_game_ready":
            // 第一把在点击游戏开始的时候来检测是否准备
            if (game.getFinishRoundCount() != 0) {
                if (fightService.checkAllReady(game)) {
                    list.add("role_game_start");
                } else {
                    list.add(WAIT);
                }
            } else {
                list.add(WAIT);
            }
            break;
        case "role_game_start":
            list.add("FlowZhuang");
            list.add("FlowGameStart");
            list.add("FlowNoticeScore");
            list.add("FlowNoticeGameStart");
            if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
                list.add("FlowGoldModeNoticeBet");
                list.add("FlowDispatch " + game.getRoleIdMap().size() + " 1 true false true false");
                list.addAll(nextAddCardProcess);
            } else {
                list.add("FlowSeat 1");
                list.addAll(noticeBetProcess);
            }
            break;
        case "role_bet":// 玩家押注后
            verifyManager.accumlate(roleGameInfoManager.current(game).verify);
            if (!checkAllBet(game)) {
                list.add("FlowSeat 1");
                list.addAll(noticeBetProcess);
            } else {
                list.add("FlowDispatch " + game.getRoleIdMap().size() + " 1 true false true false");
                list.add("FlowSeat 1");
                // 庄家的下一个就是第一个补牌的人
                list.addAll(nextAddCardProcess);
            }

            break;

        case "role_choose_add_card":// 玩家选择了是否要牌
            RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

            verifyManager.accumlate(roleGameInfo.verify);
            boolean needCard = roleGameInfo.needCard;
            if (needCard) {
                list.add("FlowAddCard " + roleGameInfo.seat + " 1");
            } else { // 选了不要
                list.add("FlowGuo");
                if (checkAllAddCard(game)) {// 进入比较流程
                    list.add("FlowRoundOver");
                } else {// 下一个人补牌
                    list.addAll(nextAddCardProcess);
                }
            }
            break;
        case "FlowZhuang":
            // 当前索引设为庄家
            game.setCurrentSeat(game.getZhuangSeat());
            break;
        case "FlowSeat":
            game.callTypeList.clear();
            break;
        case "FlowAddCard":// 补牌后
            if (isNext(game)) {
                list.add("FlowNoticeCardType");
                if (checkAllAddCard(game)) {// 所有人都补完牌了
                    list.add("FlowRoundOver");
                } else {// 下一个人补牌
                    list.addAll(nextAddCardProcess);
                }
            } else {// 继续选择
                list.add("FlowNoticeChooseAddCard");
                list.add(WAIT);
            }
            break;
        case "FlowRoundOver":
            game.callTypeList.clear();
            list.add("FlowNoticeScore");
            if (isGameOver(game)) {
                list.add("FlowGameOver");
            } else {
                // 如果是金币场,则永远不会结束，添加流程
                if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
                    list.add("FlowKickByGold");
                    list.add("FlowAddAudience " + audienceManager.getAudiences(game.getGameId()).size());
                    list.add("FlowNoticeGameRoleData");
                    list.add("FlowTimedStart");
                } else {
                    list.add(WAIT);
                }
            }
            break;
        }
        return list;
    }

    /**
     * 是否爆牌
     *
     * @param game
     * @return
     */
    private boolean isNext(Game game) {
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);
        List<Integer> cards = roleGameInfo.cards;
        game.logger.info("当前手牌: {}", cards);
        if (cards.size() >= 5) {// 最多5张牌
            return true;
        }
        TenHalfCardTypeConfig cardType = cardTypeGetter.get(cards);
        if (cardType.id == FightConstant.CARD_TYPE_BAO_PAI || cardType.number >= 105) {// 爆牌或十点半
            return true;
        }
        return false;
    }

    /**
     * 游戏是否结束
     *
     * @param game
     * @return
     */
    private boolean isGameOver(Game game) {
        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            return false;
        }
        Entity.GameConfigData gameConfigData = game.getGameConfig();

        int roundCount = gameConfigData.getRoundCount();
        int finshRoundCount = game.getFinishRoundCount();

        if (finshRoundCount >= roundCount) {
            return true;
        }
        return false;
    }

    @Override
    public CallType getCallTypeByEnum(CallTypeEnum callTypeEnum) {
        switch (callTypeEnum) {
        case GUO:
            return tenHalfGuo;
        case BET:
            return bet;
        case CHOOSE_ADD_CARD:
            return chooseAddCard;
        }
        return tenHalfGuo;
    }

    @Override
    public Class<? extends RoundInfo> getRoundInfoClass() {
        return TenHalfRoundInfo.class;
    }

    @Override
    public ZhuangCreator getZhuangCreator() {
        return tenHalfZhuangCreator;
    }

    @Override
    public ISafeCheckCallType getSafeCheckCallType() {
        return tenHalfSafeCheckCallType;
    }

    @Override
    public IReconnector<Game, Role, Message> getReconnector(Game game) {
        return tenHalfReconnector;
    }

}
