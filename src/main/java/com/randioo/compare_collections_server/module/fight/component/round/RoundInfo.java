/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.round;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description:
 * @author zsy
 * @date 2017年10月30日 上午9:37:44
 */
public class RoundInfo {
    public int overMethod;
    public List<Integer> cards = new ArrayList<>();
    /** 筹码 */
    public int score;
    /** 牌型id */
    public int cardTpyeId;
    /** 积分 */
    public int point;
}
