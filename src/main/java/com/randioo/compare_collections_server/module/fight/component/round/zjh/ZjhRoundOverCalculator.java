package com.randioo.compare_collections_server.module.fight.component.round.zjh;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;

/**
 * 回合结算计算器
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhRoundOverCalculator implements RoundOverCaculator {

	public Map<Integer, RoundInfo> getRoundResult(Game game) {
		HashMap<Integer, RoundInfo> roundResultMap = new HashMap<>();
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			// 一回合信息
			ZjhRoundInfo roundInfo = (ZjhRoundInfo) game.roundInfoMap.getRoundInfo(info.gameRoleId);
			// 总信息
			GameOverResult gameOverResult = game.getStatisticResultMap().get(info.gameRoleId);
			int point = 0;
			roundInfo.cards = info.cards;
			roundInfo.point = info.chipMoney;
			if (info.chipMoney > 0) {
				point += info.chipMoney;
				gameOverResult.score += point;
				gameOverResult.winCount++;
			} else {
				point -= info.chipMoney;
				gameOverResult.score -= point;
				gameOverResult.lossCount++;
			}
			roundResultMap.put(info.seat, roundInfo);
		}
		return roundResultMap;
	}
}
