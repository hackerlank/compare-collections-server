package com.randioo.compare_collections_server.module.fight.component.rule.zjh;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.cache.file.ZJHCardConfigCache;
import com.randioo.compare_collections_server.comparator.SeatComparator;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.file.ZJHCardConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.round.zjh.ZjhRoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.zjh.ZjhRoundOverCalculator;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.ISafeCheckCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype.ZjhBattle;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype.ZjhBigger;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype.ZjhFollow;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype.ZjhGiveUp;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype.ZjhWatch;
import com.randioo.compare_collections_server.module.fight.component.zhuang.TenHalfZhuangCreator;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Entity;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData.Builder;

/**
 * 扎金花游戏流程
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZJHRule implements ICompareGameRule<Game> {
    @Autowired
    private AudienceManager audienceManager;
    @Autowired
    private ZjhRoundOverCalculator roundOverCaculator;
    @Autowired
    private TenHalfZhuangCreator tenHalfZhuangCreator;
    @Autowired
    private FightService fightService;
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;
    @Autowired
    private ZJHSafeCheckCallType zjhSafeCheckCallType;
    @Autowired
    private SeatManager seatManager;
    @Autowired
    private ZjhReconnector zjhReconnector;
    @Autowired
    private VerifyManager verifyManager;

    public static String runningTag = "running";// 正在喊话的人
    public static String candidatesTag = "candidates";// 候选
    public static String giveUpTag = "give_up";// 弃牌
    public static String forcedGiveUpTag = "forced_give_up";// 被迫弃牌
    public static String watchCardsTag = "watch_cards";// 看牌
    public static String outWatchCardsTag = "out_watch_cards";// 看牌列表
    public static String lastFight = "last_fight";// 最后参予比赛的玩家
    public static String stepTag = "step";// 步骤
    public static int lookCardsRound = 1;// 可以看牌的阶段
    public static int topRound = 2;// 开牌阶段
    public static int roundOver = 3;// 回合结束
    @Autowired
    private ZjhBigger zjhBigger;
    @Autowired
    private ZjhFollow zjhFollow;
    @Autowired
    private ZjhWatch zjhWatch;
    @Autowired
    private ZjhGiveUp zjhGiveUp;
    @Autowired
    private ZjhBattle zjhBattle;

    @Override
    public List<String> afterCommandExecute(Game game, String flowName, String[] params) {
        List<String> list = new ArrayList<>();
        switch (flowName) {
        case "role_game_ready":
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
            list.add("FlowZhuang");// 庄家位置
            list.add("FlowGameStart");// 游戏
            list.add("FlowNoticeGameStart");// 主推游子开始
            list.add("FlowAutoBet true");// 自动下注
            list.add("FlowDispatch " + game.getRoleIdMap().size() + " 3 false false false true");// 发牌,每个人发3张牌,并且不排序
            list.add("FlowSeat 1");// 下一个人
            break;
        case "FlowAutoBet":
            // 金币场
            if (game.getGameType().getNumber() == 3) {
                game.maxChipMoney = game.getGameConfig().getBet();// 初始场上的最大注数
            } else {
                game.maxChipMoney = 1;// 初始场上的最大注数
            }
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
            List<Integer> runningSeat = game.actionSeat.get(runningTag);
            List<Integer> candidates = game.actionSeat.get(candidatesTag);
            if (runningSeat.size() > 0) {// 如果还有正在喊话的人了
                game.setCurrentSeat(runningSeat.get(0));
            } else if (runningSeat.size() == 0) {
                game.loop++;
                runningSeat.addAll(candidates);
                candidates.clear();
                Collections.sort(runningSeat,
                        new SeatComparator(game.getRoleIdMap().size(), seatManager.getNext(game, game.getZhuangSeat())));
                game.setCurrentSeat(runningSeat.get(0));
            }
            StringBuilder sb = new StringBuilder();
            if (game.loop >= game.getGameConfig().getFightRound()) {
                for (int i = 0; i < candidates.size(); i++) {
                    sb.append(candidates.get(i)).append(" ");
                }
                for (int i = 0; i < runningSeat.size(); i++) {
                    sb.append(runningSeat.get(i)).append(" ");
                }
                list.add("FlowNoticeBattle " + sb);
            }
            if (game.loop == game.getGameConfig().getTopCount()) {// 达到封顶开牌轮数
                List<Integer> step = game.actionSeat.get(stepTag);
                step.add(topRound);// 开牌阶段
                list.add("FlowNoticeBattleAll " + sb);// 主推场上没有弃牌玩家全部明牌，将进入最后比赛阶段
                String str = sb.toString();
                String[] i = str.split(" ");
                for (String strs : i) {
                    game.actionSeat.get(lastFight).add(Integer.parseInt(strs));// 最后参加比赛的玩家
                }
            }
            break;
        case "FlowCheckCallTypes":
            // 如果有叫牌则通知玩家叫牌
            if (game.callTypeList.size() > 0) {
                list.add("FlowNoticeZjhCallType");// 玩家可以进行操作的动作
                list.add(WAIT);// 等待他操作
            }
            break;
        case "watch_cards":
            list.add("FlowWatch");
            break;
        case "flow_choose_call_type":// 已经做出了选择
            // game.actionVerifyId++;// 喊话标记+1
            verifyManager.accumlate(roleGameInfoManager.current(game).verify);
            String callTypeEnumStr = params[0];
            CallTypeEnum callTypeEnum = CallTypeEnum.valueOf(callTypeEnumStr);
            switch (callTypeEnum) {
            case BIGGER:
                list.add("FlowBigger " + params[1]);
                break;
            case FOLLOW:
                list.add("FlowFollow");
                break;
            case GIVE_UP:
                list.add("FlowGiveUp");
                break;
            case BATTLE:
                list.add("FlowBattle " + params[1]);
                break;
            default:
                break;
            }
            break;
        case "FlowWatch": {// 看牌了
            int seat = game.actionSeat.get(runningTag).get(0);// 获取当前喊话的人
            game.actionSeat.get(watchCardsTag).add(seat);// 他看牌了，将当前的看牌人加到看牌队列里面去
            verifyManager.reset(roleGameInfoManager.current(game).verify);
//            roleGameInfoManager.current(game).actionVerifyId = game.actionVerifyId;
            list.add(WAIT);
        }
            break;
        case "FlowFollow": {// 跟牌了
            int seat = game.actionSeat.get(runningTag).remove(0);// 将他在正在喊话队列移除
            game.actionSeat.get(candidatesTag).add(seat);// 然后加到候选队列中
        }
            break;
        case "FlowBigger": {// 加注了
            int seat = game.actionSeat.get(runningTag).remove(0);
            game.actionSeat.get(candidatesTag).add(seat);
        }
            break;
        case "FlowGiveUp": {// 弃牌了
            game.actionSeat.get(giveUpTag).add(roleGameInfoManager.current(game).seat);// 将他加入到弃牌的队列中去
            game.actionSeat.get(runningTag).remove(0);// 并将其在喊话队列移除
            // 检查是否只剩下最后一个人，是的话直接就算这个人赢，并结算
            List<Integer> steper = game.actionSeat.get(stepTag);
            if (game.actionSeat.get(candidatesTag).size() + game.actionSeat.get(runningTag).size() == 1) {// 如果只剩一个人的话
                // 金币场
                if (game.getGameType().getNumber() == 3) {
                    list.add("FlowBattleCards");
                }
                if (!game.actionSeat.get(candidatesTag).isEmpty()) {
                    RoleGameInfo info = roleGameInfoManager.get(game, game.actionSeat.get(candidatesTag).get(0));
                    info.chipMoney += game.betPool;
                }
                if (!game.actionSeat.get(runningTag).isEmpty()) {
                    RoleGameInfo info = roleGameInfoManager.get(game, game.actionSeat.get(runningTag).get(0));
                    info.chipMoney += game.betPool;
                }
                steper.add(roundOver);
                game.getCmdStack().clear();
                list.add("FlowRoundOver");// 回合结束
            }
        }
            break;
        case "FlowBattle":
            game.actionSeat.get(runningTag).remove(0);
            List<Integer> candidate = game.actionSeat.get(candidatesTag);
            List<Integer> running = game.actionSeat.get(runningTag);
            List<Integer> step = game.actionSeat.get(stepTag);
            // 金币场
            if (candidate.size() + running.size() == 1) {// 如果场上只有一个人，那么本次操作必定会结束比赛
                game.getCmdStack().clear();
                // 金币场
                if (game.getGameType().getNumber() == 3) {
                    list.add("FlowBattleCards");
                }
                if (!candidate.isEmpty()) {
                    RoleGameInfo info = roleGameInfoManager.get(game, candidate.get(0));
                    info.chipMoney += game.betPool;
                }
                if (!running.isEmpty()) {
                    RoleGameInfo info = roleGameInfoManager.get(game, running.get(0));
                    info.chipMoney += game.betPool;
                }
                step.add(roundOver);
                list.add("FlowRoundOver");// 回合结束
            }
            break;
        case "FlowNoticeBattleAll":
            // 所有候选人,被迫弃牌的，放弃拍的都不能参加最后的比牌
            StringBuilder sber = new StringBuilder();
            for (int i = 0; i < game.actionSeat.get(lastFight).size(); i++) {
                sber.append(game.actionSeat.get(lastFight).get(i)).append(" ");
            }
            List<Integer> steps = game.actionSeat.get(stepTag);
            list.add("FlowBattleAll " + sber);
            // 金币场
            if (game.getGameType().getNumber() == 3) {
                list.add("FlowBattleCards");
            }
            steps.add(roundOver);
            list.add("FlowRoundOver");// 回合结束
            break;
        case "FlowRoundOver":
            list.add("FlowNoticeScore");
            if (isGameOver(game)) {
                list.add("FlowGameOver");
            } else {
                if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
                    list.add("FlowExitGame");
                    list.add("FlowAddAudience " + audienceManager.getAudiences(game.getGameId()).size());
                    list.add("FlowNoticeGameRoleData");
                    list.add("FlowTimedStart");
                }
                list.add("FlowNoticeReady");
                list.add(WAIT);
            }
            break;
        default:
            break;
        }
        return list;
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

    /**
     * 扎金花的牌
     */
    @Override
    public List<Integer> getCards() {
        List<Integer> cards = new ArrayList<>();
        Map<Integer, ZJHCardConfig> zjhCardMap = ZJHCardConfigCache.getZJHCardMap();
        for (Integer i : zjhCardMap.keySet()) {
            cards.add(i);
        }
        return cards;
    }

    /**
     * 检查玩家可以喊话的类型
     */
    @Override
    public void checkCallTypes(Game game) {
        // 清空喊话
        game.callTypeList.clear();
        // 获得当前的玩家
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);
        // 放弃本局或被迫放弃本局
        if (game.actionSeat.get(giveUpTag).contains(roleGameInfo.seat)
                || game.actionSeat.get(forcedGiveUpTag).contains(roleGameInfo.seat)) {
            return;
        }

        if (game.loop >= game.getGameConfig().getOutLookCount()) {// 如果可以看牌，当前轮数大于等于配置表里面的看牌轮数就可以看牌了
            List<Integer> steps = game.actionSeat.get(stepTag);
            steps.add(lookCardsRound);
            if (game.actionSeat.get(outWatchCardsTag).size() == 0) {
                game.callTypeList.add(CallTypeEnum.WATCH);// 看牌5
            }
            for (int i = 0; i < game.actionSeat.get(outWatchCardsTag).size(); i++) {
                if (game.actionSeat.get(outWatchCardsTag).get(i) != roleGameInfo.seat) {
                    game.callTypeList.add(CallTypeEnum.WATCH);// 看牌5
                }
            }
        }

        if (game.loop >= game.getGameConfig().getFightRound()) {// 如果达到可以比赛的轮数的时候
            game.callTypeList.add(CallTypeEnum.BATTLE);// 对决8
        }
        game.callTypeList.add(CallTypeEnum.FOLLOW);// 跟0
        game.callTypeList.add(CallTypeEnum.BIGGER);// 大1
        game.callTypeList.add(CallTypeEnum.GIVE_UP);// 放弃2

    }

    /**
     * 初始化队列
     */
    @Override
    public void initDataStructure(Game game) {
        game.actionSeat.put(runningTag, new ArrayList<Integer>());
        game.actionSeat.put(candidatesTag, new ArrayList<Integer>());
        game.actionSeat.put(giveUpTag, new ArrayList<Integer>());
        game.actionSeat.put(watchCardsTag, new ArrayList<Integer>());
        game.actionSeat.put(forcedGiveUpTag, new ArrayList<Integer>());
        game.actionSeat.put(outWatchCardsTag, new ArrayList<Integer>());
        game.actionSeat.put(lastFight, new ArrayList<Integer>());
        game.actionSeat.put(stepTag, new ArrayList<Integer>());
    }

    /**
     * 喊话枚举类型
     */
    @Override
    public CallType getCallTypeByEnum(CallTypeEnum callTypeEnum) {
        switch (callTypeEnum) {
        case BIGGER:
            return zjhBigger;
        case FOLLOW:
            return zjhFollow;
        case GIVE_UP:
            return zjhGiveUp;
        case WATCH:
            return zjhWatch;
        case BATTLE:
            return zjhBattle;
        default:
            return null;
        }
    }

    @Override
    public void setRoundOverInfo(Builder builder, RoundInfo roundInfo, Game game) {

    }

    @Override
    public RoundOverCaculator getRoundResult(Game game) {
        return roundOverCaculator;
    }

    @Override
    public Class<? extends RoundInfo> getRoundInfoClass() {
        return ZjhRoundInfo.class;
    }

    /**
     * 庄家类型
     */
    @Override
    public ZhuangCreator getZhuangCreator() {
        return tenHalfZhuangCreator;
    }

    @Override
    public ISafeCheckCallType getSafeCheckCallType() {
        return zjhSafeCheckCallType;
    }

    @Override
    public IReconnector<Game, Role, Message> getReconnector(Game game) {
        return zjhReconnector;
    }
}
