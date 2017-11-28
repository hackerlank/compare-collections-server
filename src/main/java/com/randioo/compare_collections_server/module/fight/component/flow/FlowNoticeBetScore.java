package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.protocol.Fight.SCFightBetScore;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

@Component
public class FlowNoticeBetScore implements Flow {

	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			SC sc = SC.newBuilder()
					.setSCFightBetScore(
							SCFightBetScore.newBuilder().setSeat(roleGameInfo.seat).setScore(roleGameInfo.betScore))
					.build();
			gameBroadcast.broadcast(game, sc);
		}
	}
}
