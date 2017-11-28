package com.randioo.compare_collections_server.module.exit.service;

import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.cache.local.VideoCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.exit.ExitConstant;
import com.randioo.compare_collections_server.module.exit.compoenent.ExitTimeEvent;
import com.randioo.compare_collections_server.module.fight.FightConstant;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.parser.GameOverProtoParser;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.ResultGameOverData;
import com.randioo.compare_collections_server.protocol.Error.ErrorCode;
import com.randioo.compare_collections_server.protocol.Fight;
import com.randioo.compare_collections_server.protocol.Fight.*;
import com.randioo.compare_collections_server.protocol.Fight.SCFightExitGame.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.util.vote.VoteBox;
import com.randioo.compare_collections_server.util.vote.VoteBox.VoteResult;
import com.randioo.randioo_server_base.annotation.BaseServiceAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.service.ObserveBaseService;
import com.randioo.randioo_server_base.template.Session;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

@BaseServiceAnnotation("exitService")
@Service("exitService")
public class ExitServiceImpl extends ObserveBaseService implements ExitService {
	@Autowired
	private EventScheduler eventScheduler;

	@Autowired
	private LoginService loginService;

	@Autowired
	private GameBroadcast gameBroadcast;

	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private FightService fightService;

	@Autowired
	private MatchService matchService;

	@Autowired
	private GameOverProtoParser gameOverProtoParser;

	@Autowired
    private AudienceManager audienceManager;

	@Autowired
    private GameManager gameManager;

	@Override
	public void agreeExit(Role role, int vote, int voteId) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightAgreeExitGameResponse(
							FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}

		SessionUtils.sc(role.getRoleId(),
				SC.newBuilder()
						.setFightAgreeExitGameResponse(
								FightAgreeExitGameResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
						.build());

		RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
		// TODO 发送给其他玩家游戏投票结果
		synchronized (game) {
			this.voteApplyExit(game, roleGameInfo.gameRoleId, voteId, vote, roleGameInfo.seat);
		}
	}

	@Override
	public void exitGame(Role role) {
		Game game = GameCache.getGameMap().get(role.getGameId());
		if (game == null) {
			FightExitGameResponse response = FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber())
					.build();
			SC sc = SC.newBuilder().setFightExitGameResponse(response).build();
			SessionUtils.sc(role.getRoleId(), sc);
			return;
		}
        boolean isGlodMode = game.getGameType() == GameType.GAME_TYPE_GOLD;

        if (!canExitGame(game, role)) {
            FightExitGameResponse response = FightExitGameResponse.newBuilder()
					.setErrorCode(ErrorCode.APPLY_REJECT.getNumber()).build();
			SC sc = SC.newBuilder().setFightExitGameResponse(response).build();
			SessionUtils.sc(role.getRoleId(), sc);
			return;
		}

        SessionUtils.sc(role.getRoleId(), SC.newBuilder()
                .setFightExitGameResponse(FightExitGameResponse.newBuilder().setErrorCode(ErrorCode.OK.getNumber()))
                .build());

        synchronized (game) {
            if (isGlodMode) {
                goldModeExitGame(game, role);
            } else {
                friendModeExitGame(game, role);
            }
            game.logger.info("=========================================");
            game.logger.info("roleID: {},name: {} 退出后", role.getRoleId(), role.getName());
            game.logger.info(game.getRoleIdMap().toString());
        }

	}

    /**
     * 能不能退出游戏
     * @param game
     * @param role
     * @return
     */
    public boolean canExitGame(Game game, Role role) {
        boolean isGlodMode = game.getGameType() == GameType.GAME_TYPE_GOLD;
        boolean gameIsStart = game.getGameState() == GameState.GAME_STATE_START;
        boolean isAudience = audienceManager.isAudience(role.getRoleId(), game.getGameId());
        if (isGlodMode) {
            if (isAudience) {
                return true;
            } else {
                return !gameIsStart;
            }
        } else {
            return !gameIsStart;
        }
    }

    /**
     * 房卡模式退出游戏
     * @param game
     * @param role
     */
    private void friendModeExitGame(Game game, Role role) {
        // 如果游戏没有开始则可以随时退出,如果是好友对战,并且是房主,则解散
        // 若是房主，则直接解散
        if (game.getMasterRoleId() == role.getRoleId()) {
            dismissGame(game, role);
        } else {
            normalexitGame(game,role);
        }
    }

    /**
     * 金币模式退出游戏
     * @param game
     * @param role
     */
    private void goldModeExitGame(Game game, Role role) {
        LinkedList<Integer> audiences = audienceManager.getAudiences(game.getGameId());
        int roleId = role.getRoleId();
        //如果是观众
        if (audiences.contains(roleId)) {
            Builder builder = SCFightExitGame.newBuilder();
            int seat = audienceManager.getSeat(role.getRoleId(),game);

            builder.setSeat(seat);
            gameBroadcast.broadcast(game, SC.newBuilder().setSCFightExitGame(builder).build());
            //移除观众
            audienceManager.remove(role.getRoleId(), game.getGameId());
        } else {
            if (game.getRoleIdMap().size() <= 1) {
                //只剩一个人了
                dismissGame(game, role);
            } else {
                normalexitGame(game,role);
            }
        }
    }

    /**
     * 在游戏间隙正常的退出游戏
     * @param game
     * @param role
     */
    private void normalexitGame(Game game, Role role) {
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, role.getRoleId());
        RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
        int seat = roleGameInfo.seat;
        // 该玩家直接退出
        Builder scFightExitGame = Fight.SCFightExitGame.newBuilder().setSeat(seat)
                .setGameRoleId(roleGameInfo.gameRoleId);

        // 座位号重新排序
        for (int i = seat + 1; i < game.getRoleIdMap().size(); i++) {
            RoleGameInfo otherRoleGameInfo = roleGameInfoManager.get(game, i);
            otherRoleGameInfo.seat -= 1;
        }
        // 移除
        game.getRoleIdMap().remove(gameRoleId);
        game.getSeatMap().clear();
        game.getRoleIdList().remove(gameRoleId);
        // 玩家退出后，通知剩余人的account
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            game.getSeatMap().put(info.seat, info);
            String account = RoleCache.getRoleById(info.roleId).getAccount();
            scFightExitGame.addAccount(account);
        }
        scFightExitGame.setExitAccount(RoleCache.getRoleById(roleGameInfo.roleId).getAccount());
        gameBroadcast.broadcast(game, SC.newBuilder().setSCFightExitGame(scFightExitGame).build());
        role.setGameId(0);
    }
    /**
     * 游戏解散
     * 房卡模式房主退出时
     * 或金币模式最后一个人退出
     * @param game
     * @param role
     */
    private void dismissGame(Game game,Role role){
        // 标记比赛结束
        game.setGameState(GameState.GAME_STATE_END);
        VideoCache.getVideoMap().remove(game.getGameId()); // 同时删除视频
        SCFightRoomDismiss scFightRoomDismiss = SCFightRoomDismiss.newBuilder().build();
        SC scFightRoomDismissSC = SC.newBuilder().setSCFightRoomDismiss(scFightRoomDismiss).build();
        // 通知房间解散
        gameBroadcast.broadcast(game, scFightRoomDismissSC);
        gameManager.destroyGame(game);
    }

    @Override
	public void applyExitGame(Role role) {
		Game game = gameManager.get(role.getGameId());
		if (game == null) {
			SessionUtils.sc(role.getRoleId(),
					SC.newBuilder().setFightApplyExitGameResponse(
							FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_NOT_EXIST.getNumber()))
							.build());
			return;
		}
		GameState gameState = game.getGameState();
		if (gameState == GameState.GAME_STATE_PREPARE || gameState == GameState.GAME_STATE_END) {
			SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightApplyExitGameResponse(
					FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_STATE_ERROER.getNumber()))
					.build());
			return;
		}

		RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());

		// 1.距离上次拒绝时间到现在的间隔时间内不能连续发起申请退出
		// 2.有人在申请退出时不能发布自己的申请退出
		int deltaTime = 0;
		int nowTime = TimeUtils.getNowTime();

		synchronized (game) {
			// 是否允许申请退出
			try {
				if (!isAllowApplyExit(nowTime, game, roleGameInfo.gameRoleId, deltaTime)) {
					SC sc = SC.newBuilder().setFightApplyExitGameResponse(
							FightApplyExitGameResponse.newBuilder().setErrorCode(ErrorCode.GAME_EXITING.getNumber()))
							.build();
					SessionUtils.sc(role.getRoleId(), sc);
					return;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}

			List<GameRoleData> list = new ArrayList<>();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				GameRoleData gameRoleData = matchService.parseGameRoleData(info, game);
				list.add(gameRoleData);
			}

			SessionUtils.sc(role.getRoleId(), SC.newBuilder().setFightApplyExitGameResponse(FightApplyExitGameResponse
					.newBuilder().setErrorCode(ErrorCode.OK.getNumber()).addAllGameRoleData(list)).build());

			VoteBox voteBox = game.getVoteBox();
			// 投票箱重置
			voteBox.reset();
			// 加入参与投票的人
			voteBox.getJoinVoteSet().addAll(game.getRoleIdMap().keySet());

			// 设置申请退出的玩家id
			int voteId = voteBox.applyVote(roleGameInfo.gameRoleId);

			SC scApplyExit = SC.newBuilder()
					.setSCFightApplyExitGame(SCFightApplyExitGame.newBuilder().setApplyExitId(voteId)
							.setSeat(roleGameInfo.seat).setCountDown(FightConstant.COUNTDOWN).addAllGameRoleData(list))
					.build();

			gameBroadcast.broadcast(game, scApplyExit);

			notifyObservers(FightConstant.FIGHT_APPLY_LEAVE, scApplyExit, game, roleGameInfo, roleGameInfo.seat);

			// 检查投票是否所有人都不在线
			if (this.checkVoteExitAllOffline(game, role.getRoleId())) {
			    gameManager.destroyGame(game);
				return;
			}
			// 订阅自动同意事件
			subsribeAutoAgreeExit(game, voteId);
		}

	}

	/**
	 * 订阅自动同意事件
	 *
	 * @param game
	 * @param voteId
	 * @author wcy 2017年11月20日
	 */
	private void subsribeAutoAgreeExit(Game game, int voteId) {
		// 投票事件
		ExitTimeEvent exitTimeEvent = new ExitTimeEvent() {

			@Override
			public void update(TimeEvent timeEvent) {
				ExitTimeEvent exitTimeEvent = (ExitTimeEvent) timeEvent;
				int gameId = exitTimeEvent.getGameId();
				int voteId = exitTimeEvent.getVoteId();
				Game game = GameCache.getGameMap().get(gameId);
				if (game == null) {
					return;
				}

				int size = game.getRoleIdMap().size();
				for (int seat = 0; seat < size; seat++) {
					String gameRoleId = game.getSeatMap().get(seat).gameRoleId;
					voteApplyExit(game, gameRoleId, voteId, ExitConstant.VOTE_AGREE, seat);
				}
			}
		};
		exitTimeEvent.setEndTime(TimeUtils.getNowTime() + FightConstant.COUNTDOWN);
		exitTimeEvent.setVoteId(voteId);
		exitTimeEvent.setGameId(game.getGameId());

		// 发送投票定时
		eventScheduler.addEvent(exitTimeEvent);
	}

	/**
	 * 检查退出所有人都不在线
	 *
	 * @return
	 * @author wcy 2017年7月24日
	 */
	private boolean checkVoteExitAllOffline(Game game, int applyRoleId) {
		int agreeExitCount = 0;
		for (RoleGameInfo roleInfo : game.getRoleIdMap().values()) {
			// 如果是机器人
			if (roleInfo.roleId == 0) {
				agreeExitCount++;
				continue;
			}
			// 不是申请人
			if (roleInfo.roleId != applyRoleId) {
				Object session = SessionCache.getSessionById(roleInfo.roleId);
				String gameRoleId = roleInfo.gameRoleId;
				VoteBox voteBox = game.getVoteBox();
				// 如果这个人没有连接并且还没有投票,则算是掉线
				Boolean result = voteBox.getVoteMap().get(gameRoleId);
				if (!Session.isConnected(session) && result == null) {
					agreeExitCount++;
				}
			}
		}
		return agreeExitCount >= game.getRoleIdMap().size() - 1;
	}

	private boolean isAllowApplyExit(int nowTime, Game game, String applyExitRoleGameId, int deltaTime) {
	    //金币模式不能申请退出
        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            return false;
        }
        VoteBox voteBox = game.getVoteBox();
		RoleGameInfo roleGameInfo = game.getRoleIdMap().get(applyExitRoleGameId);
		int lastRejectExitTime = roleGameInfo.lastRejectedExitTime;
		Role role = loginService.getRoleById(roleGameInfo.roleId);
		// 有人在申请退出时，不能让另一个人申请退出
		// 现在的时间与上次被拒绝的时间差不能小于规定间隔绿帽

		if (game.getGameType() == GameType.GAME_TYPE_FRIEND) {
			return true;
		}
		game.logger.info("applyExitGameRoleId->{}", voteBox.getApplyer());
		if (StringUtils.isNullOrEmpty(voteBox.getApplyer())) {
			game.logger.info("nowTime:{} - lastRejectExitTime:{} <= deltaTime:{}", nowTime, lastRejectExitTime,
					deltaTime);
			if (nowTime - lastRejectExitTime <= deltaTime) {
				return false;
			}
		} else {
			return false;
		}

		return true;
	}

	/**
	 *
	 * @param game
	 * @param voteGameRoleId
	 * @param applyExitId
	 * @param vote
	 * @author wcy 2017年7月17日
	 */
	private void voteApplyExit(Game game, String voteGameRoleId, int applyExitId, int vote, int seat) {
		VoteBox voteBox = game.getVoteBox();

		synchronized (voteBox) {
			if (voteBox.getVoteId() != applyExitId) {
				game.logger.info("投票箱id不一样  {}", voteBox.getVoteId());
				return;
			}

			boolean agree = vote == ExitConstant.VOTE_AGREE;
			voteBox.vote(voteGameRoleId, agree, applyExitId);

			VoteResult voteResult = voteBox.getResult();

			SC scFightNoticeAgreeExit = SC.newBuilder()
					.setSCFightNoticeAgreeExit(SCFightNoticeAgreeExit.newBuilder().setSeat(seat).setVote(vote)).build();

			// 通知所有人做出的选择
			gameBroadcast.broadcast(game, scFightNoticeAgreeExit);

			if (voteResult == VoteResult.PASS || voteResult == VoteResult.REJECT) {
				SCFightApplyExitResult.Builder builder = SCFightApplyExitResult.newBuilder();
				for (Map.Entry<String, Boolean> entrySet : voteBox.getVoteMap().entrySet()) {
					String key = entrySet.getKey();
					boolean value = entrySet.getValue();
					RoleGameInfo roleGameInfo = game.getRoleIdMap().get(key);

					if (value) {
						builder.addAgreeSeat(roleGameInfo.seat);
					} else {
						builder.addRejectSeat(roleGameInfo.seat);
					}
				}

				if (voteResult == VoteResult.PASS) {
					// 游戏结束
					ResultGameOverData result = gameOverProtoParser.parse(game);
					builder.setResultGameOverData(result);

                    gameManager.destroyGame(game);
				} else if (voteResult == VoteResult.REJECT) {
					RoleGameInfo applyerInfo = game.getRoleIdMap().get(voteBox.getApplyer());
					applyerInfo.lastRejectedExitTime = TimeUtils.getNowTime();
				}

				SC scFightApplyExitResult = SC.newBuilder().setSCFightApplyExitResult(builder).build();
				gameBroadcast.broadcast(game, scFightApplyExitResult);
				// this.notifyObservers(FightConstant.FIFHT_APPLY_EXIT_RESULT,
				// scFightApplyExitResult, game);.
				if (voteResult == VoteResult.REJECT) {
					// this.notifyObservers(FightConstant.FIGHT_REJECT_DISMISS,
					// game);
				}
				voteBox.reset();
			}
		}
	}
}
