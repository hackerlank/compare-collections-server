package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BattleCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightMaxBet;
import com.randioo.compare_collections_server.protocol.Fight.SCFightTwo;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 对决
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowBattle implements Flow {
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		int fightSeat = Integer.parseInt(params[0]);
		RoleGameInfo info = roleGameInfoManager.current(game);
		info.chipMoney -= game.maxChipMoney;
		game.betPool += game.maxChipMoney;
		List<RoleGameInfo> list = new ArrayList<>();
		list.add(roleGameInfoManager.get(game, game.getCurrentSeat()));
		list.add(roleGameInfoManager.get(game, fightSeat));
		BattleCallType callType = (BattleCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.BATTLE);
		int seat = callType.execute(game, list);
		// 金币场需要在结束后通知比牌输赢的牌
		if (game.getGameType().getNumber() == 3) {
			game.getBattleMap().put(list.get(0), list.get(1));// key=输的玩家，value=赢得玩家
		}

		if (seat == info.seat) {
			game.actionSeat.get("candidates").add(seat);
			for (int i = 0; i < game.actionSeat.get("running").size(); i++) {
				if (game.actionSeat.get("running").get(i).equals(fightSeat)) {
					game.actionSeat.get("running").remove(i);
				}
			}
			for (int i = 0; i < game.actionSeat.get("candidates").size(); i++) {
				if (game.actionSeat.get("candidates").get(i).equals(fightSeat)) {
					game.actionSeat.get("candidates").remove(i);
				}
			}
		}
		List<Integer> result = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			result.add(list.get(i).seat);
		}
		SC scFight = SC.newBuilder().setSCFightTwo(SCFightTwo.newBuilder().setFirstSeat(game.getCurrentSeat())
				.setPay(game.maxChipMoney).setSeat(seat).addAllAllSeat(result)).build();
		gameBroadcast.broadcast(game, scFight);

		SC scFightMaxBet = SC.newBuilder().setSCFightMaxBet(SCFightMaxBet.newBuilder().setMaxbets(game.maxChipMoney))
				.build();
		gameBroadcast.broadcast(game, scFightMaxBet);
	}
}
