package com.randioo.compare_collections_server.module.match.component;

import com.google.common.eventbus.AsyncEventBus;
import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.cache.local.MatchCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.event.MatchSucessEvent;
import com.randioo.randioo_server_base.config.GlobleClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Component
public class TwoQueueMatchSystem implements MatchSystem {

	private MatchRunnable runnable = new MatchRunnable();
	private ConcurrentLinkedQueue<MatchInfo> matchQueue = new ConcurrentLinkedQueue<>();
	private ConcurrentLinkedQueue<Integer> cancelQueue = new ConcurrentLinkedQueue<>();

	@Autowired
	public AsyncEventBus asyncEventBus;

	private ExecutorService executorService = Executors.newSingleThreadExecutor();

	@Override
	public void match(Role role, MatchInfo matchInfo) {
		matchQueue.add(matchInfo);
		executorService.submit(runnable);
	}

	@Override
	public void cancel(Role role) {
		cancelQueue.add(role.getRoleId());
		executorService.submit(runnable);
	}

	private void cancelMatch(int roleId) {
		MatchInfo matchInfo = null;
		for (MatchInfo info : MatchCache.getWaitQueue()) {
			if (info.roleId == roleId) {
				matchInfo = info;
			}
		}
		matchQueue.remove(matchInfo);// 排队列表删除此人
		MatchCache.getWaitQueue().remove(matchInfo);// 等待池中删除此人
		System.out.println("取消匹配后,剩余的人数 : " + MatchCache.getWaitQueue().size());
	}

	public void beginMatch(MatchInfo matchInfo) {
		GameClass gameClass = new GameClass();
		// game分类
		for (Integer gameId : GameCache.getGoldModeGameIdList()) {
			Game game = GameCache.getGameMap().get(gameId);
			gameClass.add(game);
		}
		GameList gameList = gameClass.getGameList(matchInfo.matchParameter);
		// 按这个数组顺序匹配
		List<Integer> countList = GlobleClass._G.match_sort;
		for (Integer count : countList) {
			if (gameList.hasGame(count)) {
				// 匹配到房间
				Game game = gameList.getGameByCount(count);
				MatchSucessEvent matchSuccessEvent = new MatchSucessEvent();
				matchSuccessEvent.game = game;
				matchSuccessEvent.MatchInfoList.add(matchInfo);
				asyncEventBus.post(matchSuccessEvent);
				return;
			}
		}

		// 找不到游戏，进入等待队列
		BlockingQueue<MatchInfo> waitQueue = MatchCache.getWaitQueue();
		try {
			waitQueue.put(matchInfo);
			System.out.println("当前等待的玩家数 : " + waitQueue.size());
			int matchParameter = matchInfo.matchParameter;
			int cout = 0;
			for (MatchInfo info : waitQueue) {
				if (info.matchParameter == matchParameter) {
					cout++;
				}
			}
			if (cout == GlobleClass._G.min_count) {
				// 创建房间
				MatchSucessEvent matchSucessEvent = new MatchSucessEvent();
				matchSucessEvent.MatchInfoList.addAll(waitQueue);
				waitQueue.clear();
				asyncEventBus.post(matchSucessEvent);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

	}

	private class MatchRunnable implements Runnable {

		@Override
		public void run() {
			if (!cancelQueue.isEmpty()) {
				int cancelRoleId = cancelQueue.poll();
				cancelMatch(cancelRoleId);
			}

			if (!matchQueue.isEmpty()) {
				MatchInfo matchInfo = matchQueue.poll();
				beginMatch(matchInfo);
			}
		}

	}

}
