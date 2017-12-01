package com.randioo.compare_collections_server.module.fight.component.round.cx;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.CxRule;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator.CxComparator;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator.CxPreSequenceComparator;
import com.randioo.randioo_server_base.cache.RoleCache;

@Component
public class CxRoundOverCaculator_Gold implements RoundOverCaculator {

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private CxComparator comparator;

    /**
     * 只剩下一个人
     * 
     * @param game
     * @return
     * @author wcy 2017年11月17日
     */
    private boolean checkOnlyOne(Game game) {
        List<Integer> guo = game.actionSeat.get(CxRule.guoTag);
        List<Integer> candidates = game.actionSeat.get(CxRule.candidatesTag);
        List<Integer> betAll = game.actionSeat.get(CxRule.betAllTag);
        List<Integer> running = game.actionSeat.get(CxRule.runningTag);
        return (guo.size() + candidates.size() + betAll.size() + running.size()) == 1;
    }

    @Override
    public Map<Integer, RoundInfo> getRoundResult(Game game) {

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            RoundInfo roundInfo = game.roundInfoMap.getRoundInfo(roleGameInfo.gameRoleId);
            // roundInfo.score = roleGameInfo.betScoreRecord;
            roundInfo.cards.addAll(roleGameInfo.cards);
        }

        if (game.actionSeat.get(CxRule.cutCardTag).size() > 0) {// 进入切牌
            fight(game);
        } else if (checkOnlyOne(game)) {// 只剩下一个人
            alone(game);
        }

        Map<Integer, RoundInfo> roundInfoMap = new HashMap<>();

        for (Map.Entry<String, RoundInfo> entrySet : game.roundInfoMap.getCurrentRoundMap().entrySet()) {
            String gameRoleId = entrySet.getKey();
            RoundInfo roundInfo = entrySet.getValue();

            GameOverResult gameOverResult = game.getStatisticResultMap().get(gameRoleId);
            gameOverResult.score += roundInfo.point;

            if (roundInfo.point > 0) {
                gameOverResult.winCount++;
            } else if (roundInfo.point < 0) {
                gameOverResult.lossCount++;
            }

            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

            roundInfoMap.put(roleGameInfo.seat, roundInfo);
        }

        System.out.println(roundInfoMap);
        return roundInfoMap;
    }

    private void alone(Game game) {
        List<Integer> guo = game.actionSeat.get(CxRule.guoTag);
        List<Integer> candidates = game.actionSeat.get(CxRule.candidatesTag);
        List<Integer> betAll = game.actionSeat.get(CxRule.betAllTag);
        List<Integer> running = game.actionSeat.get(CxRule.runningTag);

        List<Integer> seats = new ArrayList<>();
        seats.addAll(guo);
        seats.addAll(candidates);
        seats.addAll(betAll);
        seats.addAll(running);

        List<RoleGameInfo> roleGameInfoList = new ArrayList<>();
        // 参与比拼的人进行排序
        roleGameInfoList.add(roleGameInfoManager.get(game, seats.get(0)));

        Map<Integer, Integer> subScoreMap = new HashMap<>();

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            subScoreMap.put(roleGameInfo.seat, roleGameInfo.betScoreRecord);
        }

        List<Integer> giveUpSeats = game.actionSeat.get(CxRule.giveUpTag);
        // 加入放弃的人
        for (int seat : giveUpSeats) {
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            roleGameInfoList.add(roleGameInfo);
        }

        RoleGameInfo winner = roleGameInfoList.get(0);
        for (int j = 1; j < roleGameInfoList.size(); j++) {
            RoleGameInfo loser = roleGameInfoList.get(j);
            // 放弃的人肯定要给参赛的人钱
            RoundInfo winnerRoundInfo = game.roundInfoMap.getRoundInfo(winner.gameRoleId);
            RoundInfo loserRoundInfo = game.roundInfoMap.getRoundInfo(loser.gameRoleId);

            // 输家给赢家钱
            int minBet = Math.min(winner.betScoreRecord, loser.betScoreRecord);

            winnerRoundInfo.score += minBet;
            winnerRoundInfo.point += minBet;

            loserRoundInfo.score -= minBet;
            loserRoundInfo.point -= minBet;

            subScoreMap.put(loser.seat, subScoreMap.get(loser.seat) - minBet);
        }

        // System.out.println("差值" + game.roundInfoMap.getCurrentRoundMap());
        // // 剩余的钱还给本人
        // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
        // RoundInfo roundInfo =
        // game.roundInfoMap.getRoundInfo(roleGameInfo.gameRoleId);
        // roundInfo.score += subScoreMap.get(roleGameInfo.seat);
        // }
        // System.out.println("归还" + game.roundInfoMap.getCurrentRoundMap());

    }

    /**
     * 战斗
     * 
     * @param game
     * @author wcy 2017年11月17日
     */
    private void fight(Game game) {
        List<Integer> seats = game.actionSeat.get(CxRule.cutCardTag);
        List<RoleGameInfo> roleGameInfoList = new ArrayList<>();

        // 参与比拼的人进行排序
        for (int seat : seats) {
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            roleGameInfoList.add(roleGameInfo);
        }

        // 分数记录
        Map<Integer, Integer> scoreMap = new HashMap<>();
        Map<Integer, Integer> subScoreMap = new HashMap<>();

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            scoreMap.put(roleGameInfo.seat, roleGameInfo.betScoreRecord);
            subScoreMap.put(roleGameInfo.seat, roleGameInfo.betScoreRecord);
        }

        Comparator<RoleGameInfo> descSeqComparator = Collections.reverseOrder(new CxPreSequenceComparator());
        Collections.sort(roleGameInfoList, descSeqComparator);
        sortMessage(roleGameInfoList);
        List<Integer> giveUpSeats = game.actionSeat.get(CxRule.giveUpTag);
        // 加入放弃的人
        for (int seat : giveUpSeats) {
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            roleGameInfoList.add(roleGameInfo);
        }

        Comparator<RoleGameInfo> cxComparator = new CxComparator();
        for (int i = 0; i < roleGameInfoList.size(); i++) {
            RoleGameInfo o1 = roleGameInfoList.get(i);
            // 如果遍历到了放弃的人,则说明全部都比完了
            if (giveUpSeats.contains(o1.seat)) {
                break;
            }
            for (int j = i + 1; j < roleGameInfoList.size(); j++) {
                RoleGameInfo o2 = roleGameInfoList.get(j);
                // 放弃的人肯定要给参赛的人钱
                int value = giveUpSeats.contains(o2.seat) ? 1 : cxComparator.compare(o1, o2);
                result(o1, o2, value);
                if (value != 0) {
                    RoleGameInfo winner = value > 0 ? o1 : o2;
                    RoleGameInfo loser = value < 0 ? o1 : o2;

                    RoundInfo winnerRoundInfo = game.roundInfoMap.getRoundInfo(winner.gameRoleId);
                    RoundInfo loserRoundInfo = game.roundInfoMap.getRoundInfo(loser.gameRoleId);

                    // 输家给赢家钱
                    int minBet = Math.min(winner.betScoreRecord, scoreMap.get(loser.seat));

                    winnerRoundInfo.score += minBet;
                    winnerRoundInfo.point += minBet;
                    scoreMap.put(winner.seat, scoreMap.get(winner.seat) + minBet);

                    loserRoundInfo.score -= minBet;
                    loserRoundInfo.point -= minBet;
                    subScoreMap.put(loser.seat, subScoreMap.get(loser.seat) - minBet);

                    scoreMap.put(loser.seat, scoreMap.get(loser.seat) - minBet);

                    message(winner, loser, minBet);

                } else {
                    RoundInfo roundInfo1 = game.roundInfoMap.getRoundInfo(o1.gameRoleId);
                    RoundInfo roundInfo2 = game.roundInfoMap.getRoundInfo(o2.gameRoleId);

                    roundInfo1.score = scoreMap.get(o1.seat);
                    roundInfo2.score = scoreMap.get(o2.seat);
                }
            }
        }

        System.out.println(game.roundInfoMap.getCurrentRoundMap());
        System.out.println(scoreMap);

        // // 剩余的钱还给本人
        // for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
        // RoundInfo roundInfo =
        // game.roundInfoMap.getRoundInfo(roleGameInfo.gameRoleId);
        // roundInfo.score += subScoreMap.get(roleGameInfo.seat);
        // }
        // System.out.println(game.roundInfoMap.getCurrentRoundMap());

    }

    public void message(RoleGameInfo w, RoleGameInfo l, int money) {
        Role winRole = RoleCache.getRoleById(w.roleId);
        Role loseRole = RoleCache.getRoleById(l.roleId);

        System.out.println(winRole.getName() + " 赢了 " + loseRole.getName() + " :" + money);
    }

    public void result(RoleGameInfo o1, RoleGameInfo o2, int result) {
        Role role1 = RoleCache.getRoleById(o1.roleId);
        Role role2 = RoleCache.getRoleById(o2.roleId);

        if (result < 0) {
            System.out.println(role1.getName() + " 输给了 " + role2.getName());
        } else if (result == 0) {
            System.out.println(role1.getName() + " 平了 " + role2.getName());
        } else if (result > 0) {
            System.out.println(role1.getName() + " 赢了 " + role2.getName());
        }
    }

    public void sortMessage(List<RoleGameInfo> list) {
        StringBuilder sb = new StringBuilder();
        for (RoleGameInfo roleGameInfo : list) {
            Role r = RoleCache.getRoleById(roleGameInfo.roleId);
            sb.append(r.getAccount()).append(">");
        }
        System.out.println(sb.toString());
    }

}
