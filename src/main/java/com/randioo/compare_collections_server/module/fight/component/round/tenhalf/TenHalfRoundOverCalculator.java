/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.round.tenhalf;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardComparator;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardTypeGetter;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月30日 下午2:22:14
 */
@Component
public class TenHalfRoundOverCalculator implements RoundOverCaculator {
    @Autowired
    CardTypeGetter cardTypeGetter;

    @Autowired
    CardComparator cardComparator;

    public Map<Integer, RoundInfo> getRoundResult(Game game) {
        // 新建结果map
        // 先给roundInfo赋值
        Map<Integer, RoundInfo> roundResultMap = new HashMap<>();
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            TenHalfRoundInfo roundInfo = (TenHalfRoundInfo) game.roundInfoMap.getRoundInfo(info.gameRoleId);
			roundInfo.betMoney = info.betScore;
			roundInfo.cards = info.cards;

			roundInfo.cardType = cardTypeGetter.get(info.cards);
			roundInfo.cardTpyeId = roundInfo.cardType.id;
			roundInfo.overMethod = 1;
			roundInfo.betMoney = info.betScore;
        }
        // 回合结算
        for (RoleGameInfo zhuangRoleGameInfo : game.getRoleIdMap().values()) {
            if (zhuangRoleGameInfo.seat != game.getZhuangSeat()) {
                continue;
            }
            GameOverResult zhuangGameOverResult = game.getStatisticResultMap().get(zhuangRoleGameInfo.gameRoleId);
            // 一个玩家的该回合信息
            TenHalfRoundInfo roundInfo = (TenHalfRoundInfo) game.roundInfoMap.getRoundInfo(zhuangRoleGameInfo.gameRoleId);
            // 如果是庄家，要和其他玩家依次比较
            for (RoleGameInfo xianRoleGameInfo : game.getRoleIdMap().values()) {
                if (xianRoleGameInfo.seat == zhuangRoleGameInfo.seat) {
                    continue;
                }
                // 闲家信息
                TenHalfRoundInfo xianRoundInfo = (TenHalfRoundInfo) game.roundInfoMap.getRoundInfo(xianRoleGameInfo.gameRoleId);
                GameOverResult xianGameOverResult = game.getStatisticResultMap().get(xianRoleGameInfo.gameRoleId);
                // 庄家赢了没
                boolean zhuangWin = cardComparator.compare(zhuangRoleGameInfo.cards, xianRoleGameInfo.cards);
                if (zhuangWin) {
                    int score = xianRoleGameInfo.betScore * roundInfo.cardType.rate;
                    roundInfo.point += score;
                    xianRoundInfo.point -= score;
                    // 输赢次数
                    zhuangGameOverResult.winCount++;
                    xianGameOverResult.lossCount++;

                } else {
                    int score = xianRoleGameInfo.betScore * xianRoundInfo.cardType.rate;
                    roundInfo.point -= score;
                    xianRoundInfo.point = score;

                    zhuangGameOverResult.lossCount++;
                    xianGameOverResult.winCount++;
                }
                // 把回合分数加到最终统计上
                xianGameOverResult.score += xianRoundInfo.point;
                roundResultMap.put(xianRoleGameInfo.seat, xianRoundInfo);
            }
            zhuangGameOverResult.score += roundInfo.point;
            roundResultMap.put(zhuangRoleGameInfo.seat, roundInfo);
        }
        return roundResultMap;
    }
}
