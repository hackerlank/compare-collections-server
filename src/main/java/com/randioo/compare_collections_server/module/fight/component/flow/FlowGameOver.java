/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.RoleGameOverInfoData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGameOver;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.yiya.yiya_platform_sdk.YiyaPlatformSdk;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Description:
 * @author zsy
 * @date 2017年10月30日 下午2:38:44
 */
@Component
public class FlowGameOver implements Flow {
	@Autowired
	private MatchService matchService;

	@Autowired
	private YiyaPlatformSdk yiyaPlatformSdk;

	@Autowired
    private GameManager gameManager;
	@Override
	public void execute(Game game, String[] params) {
		game.setGameState(GameState.GAME_STATE_END);
		gameManager.destroyGame(game);
	}

	private SCFightGameOver parseGameOverData(Game game) {
		SCFightGameOver.Builder fightGameOverBuilder = SCFightGameOver.newBuilder();
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
			GameOverResult gameOverResult = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);

			RoleGameOverInfoData roleGameOverInfoData = RoleGameOverInfoData.newBuilder().setGameRoleData(gameRoleData)
					.setWinCount(gameOverResult.lossCount).setLossCount(gameOverResult.winCount)
					.setScore(gameOverResult.score).build();

			fightGameOverBuilder.addRoleGameOverInfoData(roleGameOverInfoData);
		}
		fightGameOverBuilder.setRoomId(game.getGameConfig().getRoomId());
		int maxRoundCount = game.getGameConfig().getRoundCount();
		fightGameOverBuilder.setMaxRoundCount(maxRoundCount);
		fightGameOverBuilder.setFinishRoundCount(game.getFinishRoundCount());

		// 所有人发结算通知绿
		SCFightGameOver fightGameOver = fightGameOverBuilder.build();
		return fightGameOver;
	}

	/**
	 * 增加活跃度
	 * 
	 * @param game
	 * @author wcy 2017年8月23日
	 */
	private void addRandiooActive(Game game) {

		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == 0) {
				continue;
			}

			Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
			if (role == null) {
				continue;
			}

			try {
				yiyaPlatformSdk.addActive(role.getAccount());

			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}


}
