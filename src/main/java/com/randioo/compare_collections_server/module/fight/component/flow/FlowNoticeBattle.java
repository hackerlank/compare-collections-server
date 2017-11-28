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
import com.randioo.compare_collections_server.protocol.Fight.SCFightNoticeBattle;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 通知达到了可以比赛的轮数了，主推可以比赛了(弃牌的不推)
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowNoticeBattle implements Flow {
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;
	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		List<Integer> list = new ArrayList<>();
		for (String seatStr : params) {
			int seat = Integer.parseInt(seatStr);
			RoleGameInfo info = roleGameInfoManager.get(game, seat);
			list.add(info.seat);
		}
		SC scNoticeBattle = SC.newBuilder().setSCFightNoticeBattle(SCFightNoticeBattle.newBuilder().addAllSeat(list))
				.build();
		gameBroadcast.broadcast(game, scNoticeBattle);
	}
}
