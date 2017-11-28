/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.tenhalf;

import java.util.List;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;

/**
 * @Description: 获得牌型
 * @author zsy
 * @date 2017年10月25日 上午10:51:13
 */
public interface CardTypeGetter {
	public TenHalfCardTypeConfig get(List<Integer> cards);
}
