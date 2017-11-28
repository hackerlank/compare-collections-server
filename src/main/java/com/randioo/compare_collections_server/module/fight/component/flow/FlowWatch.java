package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.WatchCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightWatchCards;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 通知客户端看牌
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowWatch implements Flow {
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;
	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		RoleGameInfo current = roleGameInfoManager.current(game);
		WatchCallType callType = (WatchCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.WATCH);
		callType.execute(game, current);
		SC scFightWatch = SC.newBuilder().setSCFightWatchCards(SCFightWatchCards.newBuilder().setSeat(current.seat))
				.build();
		// 通知其他玩家
		gameBroadcast.broadcast(game, scFightWatch);
	}
}
