package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.FollowCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGen;
import com.randioo.compare_collections_server.protocol.Fight.SCFightMaxBet;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 跟注
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowFollow implements Flow {
	@Autowired
	private GameBroadcast gameBroadcast;
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private EventBus eventBus;

	@Override
	public void execute(Game game, String[] params) {
		RoleGameInfo current = roleGameInfoManager.current(game);

		FollowCallType callType = (FollowCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.FOLLOW);
		int followAddChip = callType.execute(game, current);
		SC sc = SC.newBuilder().setSCFightGen(SCFightGen.newBuilder().setSeat(current.seat).setBets(followAddChip))
				.build();

		// 通知所有人
		gameBroadcast.broadcast(game, sc);
		SC scFightMaxBet = SC.newBuilder().setSCFightMaxBet(SCFightMaxBet.newBuilder().setMaxbets(game.maxChipMoney))
				.build();
		gameBroadcast.broadcast(game, scFightMaxBet);
	}
}