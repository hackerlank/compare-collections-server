/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.tenhalf;

import java.util.List;

/** 
* @Description: 十点半比较牌型的大小
* @author zsy  
* @date 2017年10月25日 上午11:31:03 
*/
public interface CardComparator {
    /**
     * 
     * @param zhuangCards
     * @param xianCards
     * @return true表示庄家大
     */
    public boolean compare(List<Integer> zhuangCards, List<Integer> xianCards);
}
