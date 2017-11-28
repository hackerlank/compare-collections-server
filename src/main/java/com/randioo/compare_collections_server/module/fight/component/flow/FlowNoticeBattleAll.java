package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightMingPai;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 通知全部开牌
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowNoticeBattleAll implements Flow {
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;
	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		for (String seatStr : params) {
			int seat = Integer.parseInt(seatStr);
			RoleGameInfo info = roleGameInfoManager.get(game, seat);
			SC scFightMingPai = SC.newBuilder()
					.setSCFightMingPai(SCFightMingPai.newBuilder().setSeat(info.seat).addAllCards(info.cards)).build();
			gameBroadcast.broadcast(game, scFightMingPai);
		}
	}
}
