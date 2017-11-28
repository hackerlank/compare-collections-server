package com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BiggerCallType;

/**
 * bigger
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhBigger extends BiggerCallType {
	@Override
	public int exeute(Game game, RoleGameInfo roleGameInfo, int bigMoney) {
		// 更新桌面的最大注数
		game.maxChipMoney = bigMoney;
		for (Integer i : game.actionSeat.get("watch_cards")) {
			if (i == game.getCurrentSeat()) {
				roleGameInfo.betScore += 2 * bigMoney;
				roleGameInfo.chipMoney -= 2 * bigMoney;
				roleGameInfo.betScoreRecord += 2 * bigMoney;// 赌注记录
				// 池底
				game.betPool += 2 * bigMoney;
				game.actionSeat.get("out_watch_cards").add(i);
				return 2 * bigMoney;
			}
		}
		roleGameInfo.betScoreRecord += bigMoney;// 赌注记录
		roleGameInfo.chipMoney -= bigMoney;
		roleGameInfo.betScore += bigMoney;// 玩家本局总共丢出去的筹码数
		// 池底
		game.betPool += bigMoney;
		return bigMoney;
	}
}
