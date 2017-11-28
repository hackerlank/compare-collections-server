package com.randioo.compare_collections_server.entity.bo;

import org.slf4j.Logger;

import com.google.protobuf.ByteString;
import com.randioo.randioo_server_base.entity.DefaultRole;

public class Role extends DefaultRole {

	public Logger logger;
	private int money;
	private int gameId;
	private int sex;
	private int volume = 0;
	private int musicVolume = 0;
	private String moneyExchangeTimeStr;
	private String headImgUrl;
	/** 就是元宝 */
	private int randiooMoney;
	private int randiooCard;
	private int moneyExchangeNum;
	private ByteString gameOverSC = null;
	/** 金币 */
	private int gold;
	/** 房卡 */
	private int roomCard;
	/** 比赛底分 */
	private int raceScore;
	/** 积分 */
	private int point;
	/** 是不是房主 */
	private boolean isMaster;
	/** 是不是在匹配中 */
	private boolean match;

    public boolean isMatch() {
        return match;
    }

    public void setMatch(boolean match) {
        this.match = match;
    }

    public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		this.gold = gold;
	}

	public int getRoomCard() {
		return roomCard;
	}

	public void setRoomCard(int roomCard) {
		this.roomCard = roomCard;
	}

	public boolean getIsMaster() {
		return isMaster;
	}

	public void setIsMaster(boolean isMaster) {
		this.isMaster = isMaster;
	}

	public Logger getLogger() {
		return logger;
	}

	public void setLogger(Logger logger) {
		this.logger = logger;
	}

	public int getRandiooCard() {
		return randiooCard;
	}

	public void setRandiooCard(int randiooCard) {
		this.randiooCard = randiooCard;
	}

	public int getRandiooMoney() {
		return randiooMoney;
	}

	public void setRandiooMoney(int randiooMoney) {
		this.randiooMoney = randiooMoney;
	}

	public String getHeadImgUrl() {
		return headImgUrl;
	}

	public void setHeadImgUrl(String headImgUrl) {
		this.headImgUrl = headImgUrl;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public int getMusicVolume() {
		return musicVolume;
	}

	public void setMusicVolume(int musicVolume) {
		this.musicVolume = musicVolume;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public int getGameId() {
		return gameId;
	}

	public int getSex() {
		return sex;
	}

	public void setSex(int sex) {
		this.sex = sex;
	}

	public String getMoneyExchangeTimeStr() {
		return moneyExchangeTimeStr;
	}

	public void setMoneyExchangeTimeStr(String moneyExchangeTimeStr) {
		this.moneyExchangeTimeStr = moneyExchangeTimeStr;
	}

	public int getMoneyExchangeNum() {
		return moneyExchangeNum;
	}

	public void setMoneyExchangeNum(int moneyExchangeNum) {
		this.moneyExchangeNum = moneyExchangeNum;
	}

	public int getRaceScore() {
		return raceScore;
	}

	public void setRaceScore(int raceScore) {
		this.raceScore = raceScore;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public ByteString getGameOverSC() {
		return gameOverSC;
	}

	public void setGameOverSC(ByteString gameOverSC) {
		this.gameOverSC = gameOverSC;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("Role [money=").append(money).append(", gameId=").append(gameId).append(", sex=").append(sex)
				.append(", volume=").append(volume).append(", musicVolume=").append(musicVolume)
				.append(", moneyExchangeTimeStr=").append(moneyExchangeTimeStr).append(", headImgUrl=")
				.append(headImgUrl).append(", randiooMoney=").append(randiooMoney).append(", randiooCard=")
				.append(randiooCard).append(", moneyExchangeNum=").append(moneyExchangeNum).append(", gameOverSC=")
				.append(gameOverSC).append(", raceScore=").append(raceScore).append(", point=").append(point)
				.append(", isMaster=").append(isMaster).append("]");
		return builder.toString();
	}

}
