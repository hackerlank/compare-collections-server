/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.round;

/**
 * @Description:
 * @author zsy
 * @date 2017年10月23日 下午4:57:39
 */
public class GameOverResult {
    /** 赢的次数 */
    public int winCount;
    /** 输的次数 */
    public int lossCount;
    /** 总积分 */
    public int score;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("GameOverResult [winCount=")
                .append(winCount)
                .append(", lossCount=")
                .append(lossCount)
                .append(", score=")
                .append(score)
                .append("]");
        return builder.toString();
    }

}
