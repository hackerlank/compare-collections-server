package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.rule.zjh.ZJHRule;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 游戏开始初始化
 *
 * @author Administrator
 */
@Component
public class FlowGameStart implements Flow {
	@Override
	public void execute(Game game, String[] params) {
		game.logger.info("gameStart");
		game.setGameState(GameState.GAME_STATE_START);
		game.getRule().initDataStructure(game);
		game.betPool = 0;
		game.maxChipMoney = 0;
		game.loop = 0;
		game.getBattleMap().clear();
		game.countdown = 0;
		// 设置回合结算的实现类
		game.roundInfoMap.setRoundInfoClazz(game.getRule().getRoundInfoClass());

		int baseChipMoney = game.getGameConfig().getBetMax();
		for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
			RoleGameInfo info = entrySet.getValue();
			// 叫分标记
			info.isCalled = false;
			// 最大筹码
			if (game.getRule() instanceof ZJHRule) {
				info.chipMoney = 0;
			} else {
				info.chipMoney = baseChipMoney;
			}
			// 叫的分数清空
			info.betScore = 0;
			// 下注的记录清空
			info.betScoreRecord = 0;
			// 手牌
			info.cards.clear();
			info.needCard = false;

			info.ready = false;
		}
		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			// 如果该玩家没有结果集,则创建结果集
			Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
			if (!resultMap.containsKey(info.gameRoleId)) {
				resultMap.put(info.gameRoleId, new GameOverResult());
			}
		}
		game.getRemainCards().clear();
		// 当前指向初始化
		game.setCurrentSeat(game.getZhuangSeat());
	}

}
