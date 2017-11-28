package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightPlayTalk;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 座位流程<br>
 * type 1 下一个人 2 上一个人
 *
 * @author wcy 2017年10月29日
 */
@Component
public class FlowSeat implements Flow {
	@Autowired
	private GameBroadcast gameBroadcast;
	@Autowired
	private SeatManager seatManager;

	@Override
	public void execute(Game game, String[] params) {
		int type = Integer.parseInt(params[0]);
		changeSeat(game, type);
		// TODO SC
		SC sc = SC.newBuilder().setSCFightPlayTalk(SCFightPlayTalk.newBuilder().setSeat(game.getCurrentSeat())).build();
		SessionUtils.sc(game, sc);
		// 通知其他人
		gameBroadcast.broadcast(game, sc);
		game.logger.info("当前指向: {}", game.getCurrentSeat());
	}

	/**
	 * @param game
	 * @param type
	 *            1 下一个人 2 上一个人 3当前
	 * @param targetSeat
	 *            目标座位
	 * @author wcy 2017年10月29日
	 */
	public void changeSeat(Game game, int type) {
		switch (type) {
		case 1:
			this.nextSeat(game);
			break;
		case 2:
			this.previousSeat(game);
			break;
		case 3:
			break;
		default:
			break;
		}
	}

	private void nextSeat(Game game) {
		int nextSeat = seatManager.getNext(game);
		game.setCurrentSeat(nextSeat);
	}

	private void previousSeat(Game game) {
		int prevSeat = seatManager.getPrevious(game);
		game.setCurrentSeat(prevSeat);
	}

}
