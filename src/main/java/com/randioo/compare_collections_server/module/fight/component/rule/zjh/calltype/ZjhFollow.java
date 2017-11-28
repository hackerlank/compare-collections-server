package com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.FollowCallType;

/**
 * follow
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 *
 */
@Component
public class ZjhFollow extends FollowCallType {

	@Override
	public int execute(Game game, RoleGameInfo roleGameInfo) {
		int followAddChip = game.maxChipMoney;
		// 如果看牌队列里面有人了，检查一下自己在不在里面，如果在里面
		for (Integer i : game.actionSeat.get("watch_cards")) {
			if (i == game.getCurrentSeat()) {
				roleGameInfo.betScore += 2 * followAddChip;
				roleGameInfo.chipMoney -= 2 * followAddChip;
				roleGameInfo.betScoreRecord += 2 *followAddChip;// 赌注记录
				// 池底
				game.betPool += 2 * followAddChip;
				game.actionSeat.get("out_watch_cards").add(i);
				return 2 * followAddChip;
			}
		}
		roleGameInfo.betScoreRecord += followAddChip;// 赌注记录
		roleGameInfo.betScore += followAddChip;
		roleGameInfo.chipMoney -= followAddChip;
		game.maxChipMoney = followAddChip;
		// 池底
		game.betPool += followAddChip;
		return followAddChip;
	}
}
