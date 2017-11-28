package com.randioo.compare_collections_server.module.fight.component.rule.cx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.cache.file.CXCardListConfigCache;
import com.randioo.compare_collections_server.comparator.SeatComparator;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.file.CXCardListConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.round.cx.CXRoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.round.cx.CxRoundInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.CompareGameRuleAdapter;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.ISafeCheckCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype.CxBetAll;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype.CxBigger;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype.CxFollow;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype.CxGiveUp;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype.CxGuo;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator.CxUtils;
import com.randioo.compare_collections_server.module.fight.component.zhuang.TenHalfZhuangCreator;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Entity;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData.Builder;
import com.randioo.randioo_server_base.collections8.Maps8;

/**
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 *
 */
@Component
public class CxRule extends CompareGameRuleAdapter<Game> {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;
    @Autowired
    private FightService fightService;

    @Autowired
    private SeatManager seatManager;

    @Autowired
    private CxFollow follow;

    @Autowired
    private CxBigger bigger;

    @Autowired
    private CxBetAll betAll;

    @Autowired
    private CxGuo guo;

    @Autowired
    private CxGiveUp giveUp;

    @Autowired
    private CXRoundOverCaculator roundOverCaculator;

    @Autowired
    private TenHalfZhuangCreator tenHalfZhuangCreator;

    @Autowired
    private CxSafeCheckCallType cxSafeCheckCallType;

    @Autowired
    private CxReconnector cxReconnector;

    @Autowired
    private AudienceManager audienceManager;

    public static String candidatesTag = "candidates";
    public static String guoTag = "guo";
    public static String runningTag = "running";
    public static String betAllTag = "bet_all";
    public static String giveUpTag = "give_up";
    public static String cutCardTag = "cut_card";
    public static String stepTag = "step";
    public static int 分牌阶段 = 3;
    public static int 分牌完成 = 4;
    public static int 游戏回合结束 = 5;

    @Override
    public void initDataStructure(Game game) {
        Maps8.putIfAbsent(game.actionSeat, runningTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, candidatesTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, guoTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, betAllTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, giveUpTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, cutCardTag, new ArrayList<Integer>());
        Maps8.putIfAbsent(game.actionSeat, stepTag, new ArrayList<Integer>());

        game.actionSeat.get(runningTag).clear();
        game.actionSeat.get(candidatesTag).clear();
        game.actionSeat.get(guoTag).clear();
        game.actionSeat.get(betAllTag).clear();
        game.actionSeat.get(giveUpTag).clear();
        game.actionSeat.get(cutCardTag).clear();
        game.actionSeat.get(stepTag).clear();
    }

    @Override
    public void setRoundOverInfo(Builder builder, RoundInfo roundInfo, Game game) {

    }

    @Override
    public RoundOverCaculator getRoundResult(Game game) {
        return roundOverCaculator;
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
            list.add("FlowNoticeGameStart");
            list.add("FlowAutoBet true");
            list.add("FlowNoticeScore");
            list.add("FlowNoticePublicScore");
            list.add("FlowDispatch " + game.getRoleIdMap().size() + " 2 false false true false");// 发牌,每个人发2张牌,并且不排序
            list.add("FlowSeat 1");// 下一个人
            break;
        case "FlowSeat":
            if (params[0].equals("1")) {
                List<Integer> runningSeat = game.actionSeat.get(runningTag);
                for (int i = 0; i < game.getRoleIdMap().size(); i++) {
                    runningSeat.add(i);
                }
                Collections.sort(runningSeat,
                        new SeatComparator(game.getRoleIdMap().size(), seatManager.getNext(game, game.getZhuangSeat())));
            }
            list.add("FlowCheckCallTypes");// 检查叫牌
            list.add("FlowCheckNextSeat");// 获取下一个喊话人的位置
            list.add("FlowSeat 3");// 跳转到指定的人1
            break;
        case "FlowCheckNextSeat":// 获取下一个喊话人的位置
            // 检查是否还有正在喊话的人
            List<Integer> runningSeat = game.actionSeat.get(runningTag);
            if (runningSeat.size() > 0) {// 如果还有正在喊话的人了
                game.setCurrentSeat(runningSeat.get(0));
            } else {// 没有继续喊话的人
                List<Integer> guo = game.actionSeat.get(guoTag);
                List<Integer> candidates = game.actionSeat.get(candidatesTag);
                List<Integer> betAll = game.actionSeat.get(betAllTag);
                List<Integer> step = game.actionSeat.get(stepTag);

                // 检查下注是否均衡,不均衡则继续喊话
                this.checkChipMoneyBalance(game);// 检查下注均衡
                if (runningSeat.size() == 0) {// 检查完下注均衡后如果没有需要执行的玩家
                    // 进入下一轮,如果轮数已满,则进入比较阶段
                    // 进入下一阶段条件是摸到第四张牌
                    // 将过的人也加入
                    candidates.addAll(guo);
                    guo.clear();

                    // 随便找一个还没有弃牌的玩家，如果牌数等于4，则进入下一阶段
                    RoleGameInfo roleGameInfo = roleGameInfoManager.get(game,
                            betAll.size() > 0 ? betAll.get(0) : candidates.get(0));
                    if (roleGameInfo.cards.size() == 4) {
                        step.add(分牌阶段);

                        game.getCmdStack().clear();// 栈清空
                        StringBuilder sb = new StringBuilder();
                        for (int i = 0; i < candidates.size(); i++) {
                            sb.append(candidates.get(i)).append(" ");
                        }

                        for (int i = 0; i < betAll.size(); i++) {
                            sb.append(betAll.get(i)).append(" ");
                        }
                        // 所有赌注入赌池
                        for (RoleGameInfo info : game.getRoleIdMap().values()) {
                            game.betPool += info.betScore;
                            info.betScore = 0;
                        }

                        list.add("FlowNoticeBetScore");
                        list.add("FlowNoticePublicScore");
                        list.add("FlowAdjustCards " + sb);// 进入牌组调整阶段
                        list.add(WAIT);
                    } else {// 继续下一轮发牌
                        StringBuilder touchCardTargetSeats = new StringBuilder();
                        if (candidates.size() > 0) {
                            runningSeat.addAll(candidates);
                            candidates.clear();
                            // 继续游戏的玩家排序
                            // 每次都是庄家的下一个人开始
                            Collections.sort(
                                    runningSeat,
                                    new SeatComparator(game.getRoleIdMap().size(), seatManager.getNext(game,
                                            game.getZhuangSeat())));

                            game.setCurrentSeat(runningSeat.get(0));

                            for (int seat : runningSeat) {// 接下来的执行者要再给一张牌
                                touchCardTargetSeats.append(seat).append(" ");

                                RoleGameInfo info = roleGameInfoManager.get(game, seat);
                                game.betPool += info.betScore;
                                info.betScore = 0;
                            }
                        }

                        List<Integer> betAllSeat = game.actionSeat.get(betAllTag);

                        for (int seat : betAllSeat) {// 已经赌了所有牌的玩家要再给一张牌
                            touchCardTargetSeats.append(seat).append(" ");

                            RoleGameInfo info = roleGameInfoManager.get(game, seat);
                            game.betPool += info.betScore;
                            info.betScore = 0;
                        }

                        // 仍然在游戏的每个人的赌注在到下一次发牌时加入到底池
                        game.maxChipMoney = 0;

                        // 只剩下敲的人,则直接一次性发剩余的牌
                        if (runningSeat.size() == 0 && betAllSeat.size() > 0) {
                            step.add(分牌阶段);

                            int currentCardCount = roleGameInfo.cards.size();
                            game.getCmdStack().clear();
                            list.add("FlowNoticeBetScore");
                            // 通知底池分
                            list.add("FlowNoticePublicScore");
                            // 候选人加牌
                            int addCard = 4 - currentCardCount;
                            list.add("FlowAddCard " + touchCardTargetSeats + addCard);
                            list.add("FlowAdjustCards " + touchCardTargetSeats);// 进入牌组调整阶段

                        } else {
                            list.add("FlowNoticeBetScore");
                            // 通知底池分
                            list.add("FlowNoticePublicScore");
                            // 候选人加牌
                            list.add("FlowAddCard " + touchCardTargetSeats + "1");
                        }

                    }
                } else {// 检查完下注均衡后如果有需要执行的玩家
                    // 定位到第一个
                    game.setCurrentSeat(runningSeat.get(0));
                }
            }
            break;
        case "FlowCheckCallTypes":
            // 如果有叫牌则通知玩家叫牌
            if (game.callTypeList.size() > 0) {
                list.add("FlowNoticeCxCallType");
                list.add(WAIT);
            }
            break;
        case "flow_choose_call_type":// 已经做出了选择
            game.actionVerifyId++;// 标记+1

            String callTypeEnumStr = params[0];
            CallTypeEnum callTypeEnum = CallTypeEnum.valueOf(callTypeEnumStr);

            switch (callTypeEnum) {
            case BET_ALL:
                list.add("FlowBetAll");
                break;
            case BIGGER:
                list.add("FlowBigger " + params[1]);
                break;
            case FOLLOW:
                list.add("FlowFollow");
                break;
            case GIVE_UP:
                list.add("FlowGiveUp");
                break;
            case GUO:
                list.add("FlowGuo");
                break;

            default:
                break;
            }
            break;

        case "FlowFollow": {
            int seat = game.actionSeat.get(runningTag).remove(0);
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            if (roleGameInfo.chipMoney == 0) {// 如果已经没有赌注了
                game.actionSeat.get(betAllTag).add(seat);
            } else {
                game.actionSeat.get(candidatesTag).add(seat);
            }
            list.add("FlowNoticeScore");
        }
            break;
        case "FlowBigger": {
            int seat = game.actionSeat.get(runningTag).remove(0);
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            if (roleGameInfo.chipMoney == 0) {// 如果已经没有赌注了
                game.actionSeat.get(betAllTag).add(seat);
            } else {
                game.actionSeat.get(candidatesTag).add(seat);
            }
            game.actionSeat.get(candidatesTag).addAll(game.actionSeat.get(guoTag));
            game.actionSeat.get(guoTag).clear();
            list.add("FlowNoticeScore");
        }
            break;
        case "FlowBetAll": {
            int seat = game.actionSeat.get(runningTag).remove(0);
            game.actionSeat.get(betAllTag).add(seat);
            game.actionSeat.get(candidatesTag).addAll(game.actionSeat.get(guoTag));
            game.actionSeat.get(guoTag).clear();
            list.add("FlowNoticeScore");
        }
            break;
        case "FlowGuo": {
            int seat = game.actionSeat.get(runningTag).remove(0);
            game.actionSeat.get(guoTag).add(seat);
        }
            break;
        case "FlowGiveUp": {
            game.actionSeat.get(giveUpTag).add(roleGameInfoManager.current(game).seat);
            game.actionSeat.get(runningTag).remove(0);
            List<Integer> guo = game.actionSeat.get(guoTag);
            List<Integer> candidates = game.actionSeat.get(candidatesTag);
            List<Integer> betAll = game.actionSeat.get(betAllTag);
            List<Integer> running = game.actionSeat.get(runningTag);
            List<Integer> step = game.actionSeat.get(stepTag);
            // 检查是否只剩下最后一个人，是的话直接就算这个人赢，并结算
            if (guo.size() + candidates.size() + betAll.size() + running.size() == 1) {
                step.add(游戏回合结束);

                game.getCmdStack().clear();
                list.add("FlowRoundOver false");

            } else {
                list.add("FlowNoticePublicScore");
            }

        }
            break;

        case "role_cut_cards": {// 分牌
            int cutCardSeat = Integer.parseInt(params[0]);
            List<Integer> cutCards = game.actionSeat.get(cutCardTag);
            List<Integer> candidates = game.actionSeat.get(candidatesTag);
            List<Integer> betAll = game.actionSeat.get(betAllTag);
            List<Integer> step = game.actionSeat.get(stepTag);
            // 头牌必须比尾牌大
            this.checkHeadBigger(game, cutCardSeat);

            if (cutCards.contains(cutCardSeat)) {// 如果此人已经分牌则不再进行操作
                list.add(WAIT);
                return list;
            }

            cutCards.add(cutCardSeat);
            System.out.println(game.actionSeat);
            System.out.println("cutCards" + cutCards);
            System.out.println("candidates" + candidates);
            System.out.println("betAll" + betAll);
            List<Integer> allPlayer = new ArrayList<>();
            allPlayer.addAll(candidates);
            allPlayer.addAll(betAll);
            if (cutCards.containsAll(allPlayer)) {// 如果所有人都已经分牌了
                StringBuilder sb = new StringBuilder();
                for (int i = 0; i < candidates.size(); i++) {
                    sb.append(candidates.get(i)).append(" ");
                }
                for (int i = 0; i < betAll.size(); i++) {
                    sb.append(betAll.get(i)).append(" ");
                }
                list.add("FlowOpenCards " + sb);// 主推所有人的牌给所有玩家
                step.add(分牌完成);
            } else {
                list.add(WAIT);
            }
        }
            break;
        case "FlowOpenCards":
            List<Integer> step = game.actionSeat.get(stepTag);
            step.add(游戏回合结束);
            list.add("FlowRoundOver true");// 回合结束
            break;
        case "FlowRoundOver": {
            list.add("FlowNoticeScore");
            // if (isGameOver(game)) {
            // list.add("FlowGameOver");
            // } else {
            // list.add("FlowNoticeReady");
            // list.add(WAIT);
            // }
            if (!isGameOver(game)) {
                // 如果是金币场,则永远不会结束，添加流程
                if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
                    list.add("FlowKickByGold");
                    list.add("FlowAddAudience " + audienceManager.getAudiences(game.getGameId()).size());
                    list.add("FlowNoticeGameRoleData");
                    list.add("FlowTimedStart");
                } else {
                    list.add("FlowNoticeReady");
                }
                list.add(WAIT);
            } else {
                list.add("FlowGameOver");
            }
        }
            break;
        default:
            break;
        }
        return list;

    }

    /**
     * 头牌必须比尾牌大
     * 
     * @param game
     * @param cutCardSeat
     * @author wcy 2017年11月16日
     */
    private void checkHeadBigger(Game game, int cutCardSeat) {
        RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, cutCardSeat);
        List<Integer> headCards = CxUtils.get2Cards(roleGameInfo, "head");
        List<Integer> tailCards = CxUtils.get2Cards(roleGameInfo, "tail");
        int result = CxUtils.compareCards(headCards, tailCards);

        if (result < 0) {
            roleGameInfo.cards.clear();
            roleGameInfo.cards.addAll(tailCards);
            roleGameInfo.cards.addAll(headCards);
        }
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
    public List<Integer> getCards() {
        List<Integer> cards = new ArrayList<>();
        Map<Integer, CXCardListConfig> CXCardMap = CXCardListConfigCache.getCxCardListMap();
        for (Integer i : CXCardMap.keySet()) {
            cards.add(i);
        }
        return cards;
    }

    @Override
    public void checkCallTypes(Game game) {
        // 清空喊话
        game.callTypeList.clear();
        // 获得当前的玩家
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);
        // 如果赌了全部或放弃本局
        if (roleGameInfo.chipMoney == 0 || game.actionSeat.get(giveUpTag).contains(roleGameInfo.seat)) {
            return;
        }

        int totalMoney = roleGameInfo.chipMoney + roleGameInfo.betScore;
        // 前面有喊话的人
        if (game.maxChipMoney > 0) {
            // 自己的筹码大于最大筹码就可以跟
            if (totalMoney > game.maxChipMoney) {
                game.callTypeList.add(CallTypeEnum.FOLLOW);// 0
            }
        } else {
            // 没有前一个喊话人就可以过
            game.callTypeList.add(CallTypeEnum.GUO);// 4
        }
        // 自己的筹码大于最大筹码
        if (totalMoney - game.maxChipMoney > 1) {
            game.callTypeList.add(CallTypeEnum.BIGGER);// 1
            int value = totalMoney - game.maxChipMoney;
            if (value > 3) {
                game.callTypeList.add(CallTypeEnum.BIGGER_3);
            }
            if (value > 2) {
                game.callTypeList.add(CallTypeEnum.BIGGER_2);
            }
            if (value > 1) {
                game.callTypeList.add(CallTypeEnum.BIGGER_1);
            }

        }
        game.callTypeList.add(CallTypeEnum.BET_ALL);// 3
        game.callTypeList.add(CallTypeEnum.GIVE_UP);// 2
    }

    /**
     * 下注均衡返回true
     * 
     * @param game
     * @return
     * @author wcy 2017年11月4日
     */
    private void checkChipMoneyBalance(Game game) {
        List<Integer> candidates = game.actionSeat.get(candidatesTag);
        List<Integer> needDeleteSeat = new ArrayList<>();
        // 检查下注的人是否注数相同
        for (int i = 0; i < candidates.size(); i++) {
            int seat = candidates.get(i);
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            boolean isBalance = roleGameInfo.betScore == game.maxChipMoney;
            if (!isBalance) {// 如果玩家的下注小于最大下注数,则该玩家要继续下注
                game.actionSeat.get(runningTag).add(seat);
                needDeleteSeat.add(seat);
            }
        }
        // 候选人列表去除要继续喊话的人
        candidates.removeAll(needDeleteSeat);
    }

    @Override
    public CallType getCallTypeByEnum(CallTypeEnum callTypeEnum) {
        switch (callTypeEnum) {
        case BET_ALL:
            return betAll;
        case BIGGER:
            return bigger;
        case FOLLOW:
            return follow;
        case GIVE_UP:
            return giveUp;
        case GUO:
            return guo;
        default:
            return null;
        }
    }

    @Override
    public Class<? extends RoundInfo> getRoundInfoClass() {
        return CxRoundInfo.class;
    }

    @Override
    public ZhuangCreator getZhuangCreator() {
        return tenHalfZhuangCreator;
    }

    @Override
    public ISafeCheckCallType getSafeCheckCallType() {
        return cxSafeCheckCallType;
    }

    @Override
    public IReconnector<Game, Role, Message> getReconnector(Game game) {
        return cxReconnector;
    }

}
