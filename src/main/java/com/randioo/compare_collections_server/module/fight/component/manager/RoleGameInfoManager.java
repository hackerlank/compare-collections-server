package com.randioo.compare_collections_server.module.fight.component.manager;

import java.text.MessageFormat;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.match.MatchConstant;

/**
 * 玩家信息获得者
 * 
 * @author AIM
 *
 */
@Component
public class RoleGameInfoManager {
	/** 游戏玩家id格式 */
	public static final String GAME_ROLE_ID_FORMAT = "{0}_{1}_0";
	/** 机器人玩家的格式 */
	public static final String AI_GAME_ROLE_ID_FORMAT = "{0}_0_{1}";

	/**
	 * 获得当前玩家信息F
	 * 
	 * @param game
	 * @return
	 */
	public RoleGameInfo current(Game game) {
		int index = game.getCurrentSeat();
		RoleGameInfo roleGameInfo = this.get(game, index);
		return roleGameInfo;
	}

	/**
	 * 根据座位获得玩家信息
	 * 
	 * @param game
	 * @param seat
	 * @return
	 */
	public RoleGameInfo get(Game game, int seat) {
		return game.getSeatMap().get(seat);
	}

	/**
	 * 根据房间和玩家获得id
	 * 
	 * @param game
	 * @param roleId
	 * @return
	 * @author wcy 2017年11月7日
	 */
	public RoleGameInfo getByRoleId(Game game, int roleId) {
		String gameRoleId = getGameRoleId(game, roleId);
		return game.getRoleIdMap().get(gameRoleId);
	}

	/**
	 * 获得玩家id
	 * 
	 * @param game
	 * @param roleId
	 * @return
	 * @author wcy 2017年11月7日
	 */
	public String getGameRoleId(Game game, int roleId) {
		String gameRoleId = MessageFormat.format(GAME_ROLE_ID_FORMAT, String.valueOf(game.getGameId()),
				String.valueOf(roleId));
		return gameRoleId;
	}

	/**
	 * @param gameId
	 * @param roleId
	 * @return
	 * @author wcy 2017年5月24日
	 */
	public String getAIGameRoleId(int gameId) {
		Game game = GameCache.getGameMap().get(gameId);
		int aiCount = 0;
		for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
			if (roleGameInfo.roleId == 0) {
				aiCount++;
			}
		}
		return MessageFormat.format(MatchConstant.AI_GAME_ROLE_ID_FORMAT, gameId, aiCount);
	}
}
