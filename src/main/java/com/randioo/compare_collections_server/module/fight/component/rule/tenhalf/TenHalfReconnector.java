package com.randioo.compare_collections_server.module.fight.component.rule.tenhalf;

import java.util.LinkedList;
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
import com.randioo.compare_collections_server.module.fight.component.parser.ScoreProtoParser;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.RoleCardData;
import com.randioo.compare_collections_server.protocol.Entity.SdbReconnectedData;
import com.randioo.compare_collections_server.protocol.Entity.SdbReconnectedData.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.vote.VoteBox;
import com.randioo.randioo_server_base.cache.RoleCache;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-24 13:34
 **/
@Component
public class TenHalfReconnector implements IReconnector<Game, Role, Message> {
	@Autowired
	private MatchService matchService;

	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private ScoreProtoParser scoreProtoParser;

	@Autowired
	private AudienceManager audienceManager;

	@Override
	public Message getReconnectData(Game game, Role role) {
		boolean audience = audienceManager.isAudience(role.getRoleId(), game.getGameId());
		RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
		int seat = -1;
		if (audience) {
			seat = audienceManager.getSeat(role.getRoleId(), game);
		} else {
			seat = roleGameInfo.seat;
		}

        Builder data = SdbReconnectedData.newBuilder()
                .setMySeat(seat)
                .setCurrentSeat(game.getCurrentSeat())
                .setFinishRoundCount(game.getFinishRoundCount())
                .setZhuangSeat(game.getZhuangSeat())
                .setGameConfigData(game.getGameConfig());

		for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
			RoleGameInfo info = entrySet.getValue();
			GameRoleData gameRoleData = matchService.parseGameRoleData(info, game);
			data.addGameRoleData(gameRoleData);

			// 玩家手牌
			RoleCardData roleCardData = this.protectRoleCardData(game, info);
			data.addRoleCardData(roleCardData);
			// 分数
			data.addScoreData(scoreProtoParser.parse(info));
		}

		if (!audience) {
			reconnectVoteBox(game, data);
			setCallType(game, data, roleGameInfo);
		}

		// reconnectRoundOverSC(game,data);
		LinkedList<Integer> audiences = audienceManager.getAudiences(game.getGameId());

		// 添加观众的gameRoleData
		for (int audienceId : audiences) {
			Role audienceRole = (Role) RoleCache.getRoleById(audienceId);
			GameRoleData gameRoleData = matchService.parseAudienceGameRoleData(audienceRole, game,
					audienceManager.getSeat(audienceId, game));
			data.addGameRoleData(gameRoleData);
		}
		return data.build();
	}

	private void setCallType(Game game, Builder data, RoleGameInfo roleGameInfo) {
		if (game.getGameState() == GameState.GAME_STATE_START) {
			if (game.callTypeList.contains(CallTypeEnum.BET)) {
				data.setCallType(CallTypeEnum.BET.getValue());
			} else if (game.callTypeList.contains(CallTypeEnum.CHOOSE_ADD_CARD)) {
				data.setCallType(CallTypeEnum.CHOOSE_ADD_CARD.getValue());
			}
		} else {
			data.setCallType(0);
		}
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
	private RoleCardData protectRoleCardData(Game game, RoleGameInfo roleGameInfo) {
		RoleCardData.Builder roleCardData = RoleCardData.newBuilder().setSeat(roleGameInfo.seat);
		for (int i = 0; i < roleGameInfo.cards.size(); i++) {
			// 前1张要隐身
			roleCardData.addCard(i < 1 ? 0 : roleGameInfo.cards.get(i));
		}
		return roleCardData.build();
	}

	/**
	 * 回合结算
	 *
	 * @param game
	 * @param data
	 * @author wcy 2017年11月21日
	 */
	private void reconnectRoundOverSC(Game game, SdbReconnectedData.Builder data) {
		SC roundOverSC = RoundOverSCCache.get(game.getGameId());
		if (roundOverSC != null) {
			data.addAllRoleRoundOverInfoData(roundOverSC.getSCFightRoundOver().getRoleRoundOverInfoDataList());
		}
	}

	/**
	 * 投票箱
	 *
	 * @param game
	 * @param data
	 * @author wcy 2017年11月21日
	 */
	private void reconnectVoteBox(Game game, SdbReconnectedData.Builder data) {
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
}
