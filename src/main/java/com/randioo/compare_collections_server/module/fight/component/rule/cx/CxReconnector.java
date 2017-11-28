package com.randioo.compare_collections_server.module.fight.component.rule.cx;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.cache.local.RoundOverSCCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.CxReconnectedData;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.ResultGameOverData;
import com.randioo.compare_collections_server.protocol.Entity.RoleCardData;
import com.randioo.compare_collections_server.protocol.Entity.ScoreData;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.vote.VoteBox;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class CxReconnector implements IReconnector<Game, Role, Message> {

	@Autowired
	private MatchService matchService;

	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Override
	public Message getReconnectData(Game game, Role role) {
		List<Integer> giveUpSeats = game.actionSeat.get(CxRule.giveUpTag);
		List<Integer> step = game.actionSeat.get(CxRule.stepTag);
		List<Integer> cutCards = game.actionSeat.get(CxRule.cutCardTag);

		List<Integer> callTypes = new ArrayList<>();
		for (CallTypeEnum callTypeEnum : game.callTypeList) {
			callTypes.add(callTypeEnum.getValue());
		}

		RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());

		int cutCardsState = checkCutCardsState(giveUpSeats, step, cutCards, roleGameInfo.seat);

		CxReconnectedData.Builder data = CxReconnectedData.newBuilder().setGameConfigData(game.getGameConfig())
				.setFinishRoundCount(game.getFinishRoundCount())
				.setMySeat(roleGameInfoManager.getByRoleId(game, role.getRoleId()).seat).addAllCallTypes(callTypes)
				.setCallSeat(game.getCurrentSeat()).addAllGiveUpSeat(giveUpSeats).setCutCardsState(cutCardsState)
				.setBasePool(game.betPool);

		// 恢复回合结束
		reconnectRoundOverSC(game, data);

		// 恢复投票箱
		reconnectVoteBox(game, data);

		for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
			RoleGameInfo info = entrySet.getValue();
			GameRoleData gameRoleData = matchService.parseGameRoleData(info, game);
			data.addGameRoleData(gameRoleData);

			// 玩家手牌
			RoleCardData roleCardData = this.protectRoleCardData(game, roleGameInfo, info);
			data.addRoleCardData(roleCardData);
			// 玩家桌面赌注
			data.addTableChips(ScoreData.newBuilder().setChipMoney(info.betScore).setSeat(info.seat));
			// 玩家手中筹码
			GameOverResult result = game.getStatisticResultMap().get(info.gameRoleId);
			int score = result != null ? result.score : 0;

			data.addRoleChips(ScoreData.newBuilder().setChipMoney(info.chipMoney).setSeat(info.seat).setScore(score));
		}
		return data.build();
	}

	/**
	 * 回合结算
	 * 
	 * @param game
	 * @param data
	 * @author wcy 2017年11月21日
	 */
	private void reconnectRoundOverSC(Game game, CxReconnectedData.Builder data) {
		SC roundOverSC = RoundOverSCCache.get(game.getGameId());
		if (roundOverSC != null) {
			data.addAllRoleRoundOverInfoData(roundOverSC.getSCFightRoundOver().getRoleRoundOverInfoDataList());
			ResultGameOverData resultGameOverData = roundOverSC.getSCFightRoundOver().getResultGameOverData();
			if (resultGameOverData != null) {
				data.setResultGameOverData(resultGameOverData);
			}
		}
	}

	/**
	 * 投票箱
	 * 
	 * @param game
	 * @param data
	 * @author wcy 2017年11月21日
	 */
	private void reconnectVoteBox(Game game, CxReconnectedData.Builder data) {
		VoteBox voteBox = game.getVoteBox();
		// 检查是否有申请退出,如果有,投票箱的参与人数一定是大于0的
		if (voteBox.getJoinVoteSet().size() > 0) {
			int applySeat = game.getRoleIdMap().get(voteBox.getApplyer()).seat;
			data.setApplyExitSeat(applySeat);
			data.addAgreeSeat(applySeat);
			data.setExitApplyId(voteBox.getVoteId());

			Map<String, Boolean> map = voteBox.getVoteMap();
			for (Map.Entry<String, Boolean> entrySet : map.entrySet()) {
				String gameRoleId = entrySet.getKey();
				Boolean vote = entrySet.getValue();
				int seat = game.getRoleIdMap().get(gameRoleId).seat;
				if (vote) {
					data.addAgreeSeat(seat);
				} else {
					data.addRejectSeat(seat);
				}
			}
		}
	}

	/**
	 * 检查切牌状态
	 * 
	 * @param giveUpSeats
	 * @param step
	 * @param cutCards
	 * @param seat
	 * @return
	 * @author wcy 2017年11月20日
	 */
	private int checkCutCardsState(List<Integer> giveUpSeats, List<Integer> step, List<Integer> cutCards, int seat) {
		int cutCardsState = 0;
		// 如果在分派阶段
		if (step.contains(CxRule.分牌阶段) && !step.contains(CxRule.游戏回合结束) && !giveUpSeats.contains(seat)) {
			cutCardsState = 1;
			// 如果已经切完牌,必定已经在数组中
			if (cutCards.contains(seat)) {
				cutCardsState = 2;
			}
		}
		return cutCardsState;
	}

	/**
	 * 保护玩家的卡牌
	 * 
	 * @param game
	 * @param myRoleGameInfo
	 * @param roleGameInfo
	 * @return
	 * @author wcy 2017年11月20日
	 */
	private RoleCardData protectRoleCardData(Game game, RoleGameInfo myRoleGameInfo, RoleGameInfo roleGameInfo) {
		RoleCardData.Builder roleCardData = RoleCardData.newBuilder().setSeat(roleGameInfo.seat);
		if (myRoleGameInfo == roleGameInfo || game.actionSeat.get(CxRule.stepTag).contains(CxRule.游戏回合结束)) {
			return roleCardData.addAllCard(roleGameInfo.cards).setSeat(roleGameInfo.seat).build();
		}
		for (int i = 0; i < roleGameInfo.cards.size(); i++) {
			// 前两张要隐身
			roleCardData.addCard(i < 2 ? 0 : roleGameInfo.cards.get(i));
		}
		return roleCardData.build();

	}
}
