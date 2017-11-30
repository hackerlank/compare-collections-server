package com.randioo.compare_collections_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.randioo.randioo_server_base.entity.Verify;

public class RoleGameInfo {
    /**
     * 游戏中的玩家id
     */
    public String gameRoleId;
    /**
     * 座位索引
     */
    public int seat;
    /**
     * 全局玩家id
     */
    public int roleId;
    /**
     * 手上别人看不到的牌
     */
    public List<Integer> cards = new ArrayList<>();
    /**
     * 压过注没
     */
    public boolean isCalled;
    /**
     * 叫的分数
     */
    public int betScore;
    /**
     * 下注的记录
     */
    public int betScoreRecord;
    /**
     * 新拿的牌
     */
    public int newCard;
    /**
     * 是否准备完成
     */
    public boolean ready;
    /**
     * 自动出牌标记
     */
    public int auto;
    /**
     * 申请退出时间
     */
    public int lastRejectedExitTime;
    /**
     * 是否继续要牌
     */
    public boolean needCard;
    /**
     * 每个人摸的牌
     */
    public int everybodyTouchCard;
    /**
     * 筹码
     */
    public int chipMoney;
    /**
     * 房卡
     */
    public int rdiooCard;
    /**
     * 动作校验ID
     */
    public int actionVerifyId;
    /**
     * 是不是房主
     */
    public boolean isMaster;
    // 离开状态
    public boolean leave;

    public Map<String, Boolean> isWatchCards;

    public Verify verify = new Verify();

    public String print() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleGameInfo [gameRoleId=")
                .append(gameRoleId)
                .append(", roleId=")
                .append(roleId)
                .append(", cards=")
                .append(cards)
                .append("]");
        return builder.toString();
    }

    public String string() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoleGameInfo [gameRoleId=")
                .append(gameRoleId)
                .append(", seat=")
                .append(seat)
                .append(", roleId=")
                .append(roleId)
                .append(", cards=")
                .append(cards)
                .append(", isCalled=")
                .append(isCalled)
                .append(", betScore=")
                .append(betScore)
                .append(", betScoreRecord=")
                .append(betScoreRecord)
                .append(", newCard=")
                .append(newCard)
                .append(", ready=")
                .append(ready)
                .append(", auto=")
                .append(auto)
                .append(", lastRejectedExitTime=")
                .append(lastRejectedExitTime)
                .append(", needCard=")
                .append(needCard)
                .append(", everybodyTouchCard=")
                .append(everybodyTouchCard)
                .append(", chipMoney=")
                .append(chipMoney)
                .append(", rdiooCard=")
                .append(rdiooCard)
                .append(", actionVerifyId=")
                .append(actionVerifyId)
                .append(", isMaster=")
                .append(isMaster)
                .append(", isWatchCards=")
                .append(isWatchCards)
                .append("]");
        return builder.toString();
    }

    @Override
    public String toString() {
        return print();
    }

}
