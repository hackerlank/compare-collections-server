package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGameStartCountdown;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.scheduler.timeevent.GameStartEvent;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * @author zsy
 * @Description: 定时开始游戏
 * @create 2017-11-22 14:39
 **/
@Component
public class FlowTimedStart implements Flow {
	@Autowired
	private EventScheduler eventScheduler;

	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		GameStartEvent gameStartEvent = new GameStartEvent(game);
		gameStartEvent.setEndTime(GlobleClass._G.start_time + TimeUtils.getNowTime());

		eventScheduler.addEvent(gameStartEvent);

		gameBroadcast.broadcast(game,
				SC.newBuilder()
						.setSCFightGameStartCountdown(
								SCFightGameStartCountdown.newBuilder().setCountdown(GlobleClass._G.start_time))
						.build());
	}
}
