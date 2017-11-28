package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BiggerCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightBigger;
import com.randioo.compare_collections_server.protocol.Fight.SCFightMaxBet;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 加注
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowBigger implements Flow {
	@Autowired
	private GameBroadcast gameBroadcast;
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Override
	public void execute(Game game, String[] params) {
		int biggerMoney = Integer.parseInt(params[0]);
		RoleGameInfo current = roleGameInfoManager.current(game);

		BiggerCallType callType = (BiggerCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.BIGGER);
		int needBet = callType.exeute(game, current, biggerMoney);
		SC sc = SC.newBuilder().setSCFightBigger(SCFightBigger.newBuilder().setSeat(current.seat).setBets(needBet))
				.build();
		// 通知其他人
		gameBroadcast.broadcast(game, sc);

		SC scFightMaxBet = SC.newBuilder().setSCFightMaxBet(SCFightMaxBet.newBuilder().setMaxbets(game.maxChipMoney))
				.build();
		gameBroadcast.broadcast(game, scFightMaxBet);
	}
}
