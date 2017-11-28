/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.round.tenhalf;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;

/** 
* @Description: 一回合的信息
* @author zsy  
* @date 2017年10月23日 下午4:39:46 
*/
public class TenHalfRoundInfo extends RoundInfo {
    /**押注金额*/
    public int betMoney;
    /**牌型*/
    public TenHalfCardTypeConfig cardType;
}
