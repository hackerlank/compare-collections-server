package com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype;

import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.ZjhComparator.ZjhComparator;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BattleCallType;

/**
 * 对决
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhBattle extends BattleCallType {

	@Override
	public int execute(Game game, List<RoleGameInfo> list) {
		Collections.sort(list, new ZjhComparator());
		// 第二个玩家是胜利的
		game.actionSeat.get("forced_give_up").add(list.get(0).seat);
		return list.get(1).seat;
	}
}
