package com.randioo.compare_collections_server.entity.po;

import com.randioo.compare_collections_server.entity.po.env_vars.EnvVars;
import com.randioo.compare_collections_server.module.fight.component.AbstractCompareGame;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfoMap;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Entity.FightVoteApplyExit;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.util.vote.VoteBox;
import com.randioo.randioo_server_base.module.key.Key;

import java.util.*;

public class Game extends AbstractCompareGame {

	private int gameId;
	/** 玩家id集合 */
	private Map<String, RoleGameInfo> roleIdMap = new LinkedHashMap<>();
	/** 房主id */
	private int masterRoleId;
	/** 房间锁 */
	private Key lockKey;
	/** 座位表 */
	private Map<Integer, RoleGameInfo> seatMap = new HashMap<>();
	/** 游戏开始 */
	private GameState gameState;
	/** 游戏类型 */
	private GameType gameType;
	/** 游戏配置 */
	private GameConfigData gameConfigData;
	/** 在线玩家数量 */
	private int onlineRoleCount;
	/** 当前玩家id */
	private int currentSeat;
	/** 动作校验ID */
	public int actionVerifyId;
	/** 庄家的玩家id */
	private int zhuangSeat;
	/** 以及打完的回合数 */
	private int finishRoundCount;
	/** 剩余的牌 */
	private List<Integer> remainCards = new ArrayList<>();
	/** 投票箱 */
	private VoteBox voteBox = new VoteBox();
	/** 玩家结果统计 */
	private Map<String, GameOverResult> statisticResultMap = new HashMap<>();
	/** 环境变量集合 */
	public EnvVars envVars = new EnvVars();
	/** 分数池 */
	public int betPool;
	public List<CallTypeEnum> callTypeList = new ArrayList<>();
	/** 正在申请退出的玩家id */
	private String applyExitGameRoleId;
	/** 申请退出的次数 */
	private int applyExitId;
	/** 表决表 */
	private Map<String, FightVoteApplyExit> voteMap = new HashMap<>();
	/** 扎金花比赛结果 */
	private Map<RoleGameInfo, RoleGameInfo> battleMap = new HashMap<>();
    //金币场匹配参数
    public int matchParameter;
    //计时结束的时间
    public int countdown;

	public Map<RoleGameInfo, RoleGameInfo> getBattleMap() {
		return battleMap;
	}

	public void setBattleMap(Map<RoleGameInfo, RoleGameInfo> battleMap) {
		this.battleMap = battleMap;
	}

	// 玩家id列表，用于换人
	private List<String> roleIdList = new ArrayList<>();

	public List<String> getRoleIdList() {
		return roleIdList;
	}

	public Map<String, FightVoteApplyExit> getVoteMap() {
		return voteMap;
	}

	public void setVoteMap(Map<String, FightVoteApplyExit> voteMap) {
		this.voteMap = voteMap;
	}

	public int getApplyExitId() {
		return applyExitId;
	}

	public void setApplyExitId(int applyExitId) {
		this.applyExitId = applyExitId;
	}

	public String getApplyExitGameRoleId() {
		return applyExitGameRoleId;
	}

	public void setApplyExitGameRoleId(String applyExitGameRoleId) {
		this.applyExitGameRoleId = applyExitGameRoleId;
	}

	// 准备集
	public Set<Integer> readySet = new HashSet<>();
	public int loop;
	private int count;// 桌上剩下人的数量（针对放弃比赛的玩家）
	/** 最大下注数 */
	public int maxChipMoney;
	/** 回合结算表 */
	public RoundInfoMap roundInfoMap = new RoundInfoMap();

	/** 动作队列 */
	public Map<String, List<Integer>> actionSeat = new HashMap<>();

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public GameConfigData getGameConfig() {
		return gameConfigData;
	}

	public void setGameConfig(GameConfigData gameConfigData) {
		this.gameConfigData = gameConfigData;
	}

	public Map<String, GameOverResult> getStatisticResultMap() {
		return statisticResultMap;
	}

	public VoteBox getVoteBox() {
		return voteBox;
	}

	public int getFinishRoundCount() {
		return finishRoundCount;
	}

	public void setFinishRoundCount(int finishRoundCount) {
		this.finishRoundCount = finishRoundCount;
	}

	public int getOnlineRoleCount() {
		return onlineRoleCount;
	}

	public void setOnlineRoleCount(int onlineRoleCount) {
		this.onlineRoleCount = onlineRoleCount;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public Map<String, RoleGameInfo> getRoleIdMap() {
		return roleIdMap;
	}

	public int getMasterRoleId() {
		return masterRoleId;
	}

	public void setMasterRoleId(int masterRoleId) {
		this.masterRoleId = masterRoleId;
	}

	public Key getLockKey() {
		return lockKey;
	}

	public void setLockKey(Key lockKey) {
		this.lockKey = lockKey;
	}

	public GameType getGameType() {
		return gameType;
	}

	public void setGameType(GameType gameType) {
		this.gameType = gameType;
	}

	public GameState getGameState() {
		return gameState;
	}

	public void setGameState(GameState gameState) {
		this.gameState = gameState;
	}

	/**
	 * 获得座位表
	 *
	 * @return
	 * @author wcy 2017年9月23日
	 */
	public Map<Integer, RoleGameInfo> getSeatMap() {
		return seatMap;
	}

	public int getCurrentSeat() {
		return currentSeat;
	}

	public void setCurrentSeat(int currentSeat) {
		this.currentSeat = currentSeat;
	}

	public int getZhuangSeat() {
		return zhuangSeat;
	}

	public void setZhuangSeat(int zhuangSeat) {
		this.zhuangSeat = zhuangSeat;
	}

	/**
	 * 获得剩余牌
	 *
	 * @return
	 */
	public List<Integer> getRemainCards() {
		return remainCards;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Game [gameId=").append(gameId).append(", roleIdMap=").append(roleIdMap)
				.append(", masterRoleId=").append(masterRoleId).append(", lockKey=").append(lockKey)
				.append(", seatMap=").append(seatMap).append(", gameState=").append(gameState).append(", gameType=")
				.append(gameType).append(", gameConfigData=").append(gameConfigData).append(", onlineRoleCount=")
				.append(onlineRoleCount).append(", currentSeat=").append(currentSeat).append(", actionVerifyId=")
				.append(actionVerifyId).append(", zhuangSeat=").append(zhuangSeat).append(", finishRoundCount=")
				.append(finishRoundCount).append(", remainCards=").append(remainCards).append(", voteBox=")
				.append(voteBox).append(", statisticResultMap=").append(statisticResultMap).append(", envVars=")
				.append(envVars).append(", betPool=").append(betPool).append(", callTypeList=").append(callTypeList)
				.append(", applyExitGameRoleId=").append(applyExitGameRoleId).append(", applyExitId=")
				.append(applyExitId).append(", voteMap=").append(voteMap).append(", roleIdList=").append(roleIdList)
				.append(", readySet=").append(readySet).append(", loop=").append(loop).append(", count=").append(count)
				.append(", maxChipMoney=").append(maxChipMoney).append(", roundInfoMap=").append(roundInfoMap)
				.append(", actionSeat=").append(actionSeat).append("]");
		return builder.toString();
	}

}
