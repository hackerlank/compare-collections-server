/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeRoundOver;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.parser.GameOverProtoParser;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.CardData;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightRoundOver;
import com.randioo.compare_collections_server.protocol.Fight.SCFightRoundOver.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月25日 上午10:40:54
 */
@Component
public class FlowRoundOver implements Flow {

	@Autowired
	RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private MatchService matchService;

	@Autowired
	GameBroadcast gameBroadcast;

	@Autowired
	private EventBus eventBus;

	@Autowired
	private GameOverProtoParser gameOverProtoParser;

	@Override
	public void execute(Game game, String[] params) {
	    game.setGameState(GameState.GAME_STATE_WAIT);
		// 回合数+1
		int finishRoundCount = game.getFinishRoundCount() + 1;
		game.setFinishRoundCount(finishRoundCount);

		ICompareGameRule<Game> rule = game.getRule();

		game.roundInfoMap.setCurrentRoundCount(finishRoundCount);
		game.roundInfoMap.initRoundInfo(game.getRoleIdMap().keySet());

		rule.getRoundResult(game).getRoundResult(game);

		// 回合信息
		Builder scFightRoundOver = SCFightRoundOver.newBuilder();

		for (RoleGameInfo info : game.getRoleIdMap().values()) {
			RoundInfo roundInfo = game.roundInfoMap.getRoundInfo(info.gameRoleId);
			int betMax = game.getGameConfig().getBetMax();
			// int chipMoney = roundInfo.score + info.chipMoney - betMax;
			int chipMoney = roundInfo.score;
			// System.out.println(RoleCache.getRoleById(info.roleId).getAccount()
			// + " 归还后的值:" + roundInfo.score
			// + " 手上没有下的注：" + info.chipMoney + " 配置表最大赌注：" + betMax + " =" +
			// chipMoney);
			int roundScore = game.getStatisticResultMap().get(info.gameRoleId).score;
			// 每个人回合信息
			RoleRoundOverInfoData.Builder roleRoundOverInfoDataRuilder = RoleRoundOverInfoData.newBuilder()
					.setGameRoleData(matchService.parseGameRoleData(info, game))
					.setRoleId(info.roleId)
					.setBetMoney(info.betScoreRecord)
					.setCardData(CardData.newBuilder().addAllCards(roundInfo.cards).setCardType(roundInfo.cardTpyeId))
					.setOverMethod(roundInfo.overMethod).setRoundScore(roundScore).setChipMoney(chipMoney)
					.setZhuang(info.seat == game.getZhuangSeat());

			// 设置每个游戏的特殊信息
			rule.setRoundOverInfo(roleRoundOverInfoDataRuilder, roundInfo, game);

			scFightRoundOver.addRoleRoundOverInfoData(roleRoundOverInfoDataRuilder);
		}

		// 如果是最后一局，设置二次结算
        if (finishRoundCount == game.getGameConfig().getRoundCount() && game
                .getGameType() == GameType.GAME_TYPE_FRIEND) {
            scFightRoundOver.setResultGameOverData(gameOverProtoParser.parse(game));
        }
        SC sc = SC.newBuilder()
				.setSCFightRoundOver(scFightRoundOver.setRoomId(game.getGameConfig().getRoomId())
						.setFinishRoundCount(finishRoundCount).setMaxRoundCount(game.getGameConfig().getRoundCount()))
				.build();

		// 发送
		gameBroadcast.broadcast(game, sc);
		eventBus.post(new EventNoticeRoundOver(game, sc));
	}

}
