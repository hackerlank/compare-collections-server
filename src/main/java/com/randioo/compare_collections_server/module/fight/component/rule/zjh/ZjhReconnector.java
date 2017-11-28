package com.randioo.compare_collections_server.module.fight.component.rule.zjh;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.cache.local.RoundOverSCCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.ResultGameOverData;
import com.randioo.compare_collections_server.protocol.Entity.RoleCardData;
import com.randioo.compare_collections_server.protocol.Entity.ScoreData;
import com.randioo.compare_collections_server.protocol.Entity.ZjhReconnectedData;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.vote.VoteBox;
import com.randioo.randioo_server_base.cache.RoleCache;

/**
 * 扎金花断线数据恢复
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhReconnector implements IReconnector<Game, Role, Message> {
	@Autowired
	private MatchService matchService;
	@Autowired
	private RoleGameInfoManager roleGameInfoManager;
	@Autowired
	private AudienceManager audienceManager;

	@Override
	public Message getReconnectData(Game game, Role role) {
		List<Integer> giveUpSeats = game.actionSeat.get(ZJHRule.giveUpTag);// 放弃列表
		List<Integer> lookCards = game.actionSeat.get(ZJHRule.watchCardsTag);// 看牌列表
		List<Integer> ficeGiveUp = game.actionSeat.get(ZJHRule.forcedGiveUpTag);// 被迫弃牌列表
		// 喊话类型
		List<Integer> callTypes = new ArrayList<>();
		for (CallTypeEnum callTypeEnum : game.callTypeList) {
			callTypes.add(callTypeEnum.getValue());
		}
		RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());

		boolean audience = audienceManager.isAudience(role.getRoleId(), game.getGameId());
		int seat = -1;
		if (audience) {
			seat = audienceManager.getSeat(role.getRoleId(), game);
		} else {
			seat = roleGameInfo.seat;
		}
		// 包装恢复游戏数据
		ZjhReconnectedData.Builder data = ZjhReconnectedData.newBuilder()//
				.setGameConfigData(game.getGameConfig())// 游戏配置数据
				.setFinishRoundCount(game.getFinishRoundCount())// 完成的局数
				.setMySeat(seat)// 自己的位置
				.addAllCallTypes(callTypes)// 喊话类型
				.setCallSeat(game.getCurrentSeat())// 喊话的位置
				.addAllGiveUpSeat(giveUpSeats)// 放弃的玩家的位置
				.addAllFiceGiveUpSeat(ficeGiveUp)// 被迫弃牌的位置
				.addAllLookCardsSeat(lookCards)// 看牌的玩家位置
				.setBasePool(game.betPool)// 赌池
				.setStepState(game.loop);// 阶段就是当局比赛的轮数后玩家可以操作什么的意思
		// 恢复回合结束
		reconnectRoundOverSC(game, data);
		reconnectVoteBox(game, data);
		// 包装返回玩家本局游戏信息给客户端
		for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
			RoleGameInfo info = entrySet.getValue();
			GameRoleData gameRoleData = matchService.parseGameRoleData(info, game);
			data.addGameRoleData(gameRoleData);
			// 玩家手牌
			RoleCardData roleCardData = this.protectRoleCardData(game, roleGameInfo, info);
			data.addRoleCardData(roleCardData);
			// 玩家桌面赌注(玩家本局丢出去的筹码数)
			data.addTableChips(ScoreData.newBuilder().setChipMoney(info.betScore).setSeat(info.seat));
			// 玩家手中筹码
			GameOverResult result = game.getStatisticResultMap().get(info.gameRoleId);
			int score = result != null ? result.score : 0;
			data.addRoleChips(ScoreData.newBuilder().setChipMoney(info.chipMoney).setSeat(info.seat).setScore(score));
		}
		// 添加观众的gameRoleData
		LinkedList<Integer> audiences = audienceManager.getAudiences(game.getGameId());
		for (int audienceId : audiences) {
			Role audienceRole = (Role) RoleCache.getRoleById(audienceId);
			GameRoleData gameRoleData = matchService.parseAudienceGameRoleData(audienceRole, game,
					audienceManager.getSeat(audienceId, game));
			data.addGameRoleData(gameRoleData);
			data.addRoleChips(ScoreData.newBuilder().setChipMoney(audienceRole.getGold())
					.setSeat(audienceManager.getSeat(audienceId, game)).setScore(0));
		}
		// 推送
		return data.build();
	}

	/**
	 * 回合结算
	 * 
	 * @param game
	 * @param data
	 */
	private void reconnectRoundOverSC(Game game, ZjhReconnectedData.Builder data) {
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
	 * 检查是否有申请退出
	 * 
	 * @param game
	 * @param data
	 */
	private void reconnectVoteBox(Game game, ZjhReconnectedData.Builder data) {
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
	 * 保护玩家的卡牌
	 * 
	 * @param game
	 * @param myRoleGameInfo
	 * @param roleGameInfo
	 * @return
	 */
	private RoleCardData protectRoleCardData(Game game, RoleGameInfo myRoleGameInfo, RoleGameInfo roleGameInfo) {
		RoleCardData.Builder roleCardData = RoleCardData.newBuilder().setSeat(roleGameInfo.seat);
		if (myRoleGameInfo == roleGameInfo || game.actionSeat.get(ZJHRule.stepTag).contains(ZJHRule.roundOver)) {
			return roleCardData.addAllCard(roleGameInfo.cards).setSeat(roleGameInfo.seat).build();
		}
		for (int i = 0; i < roleGameInfo.cards.size(); i++) {
			// 扎金花玩家每个人获得3张卡牌，并对卡牌进行保护处理,也就是将卡牌置0
			roleCardData.addCard((i < 3) ? 0 : roleGameInfo.cards.get(i));
		}
		return roleCardData.build();
	}
}
