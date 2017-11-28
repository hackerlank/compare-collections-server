/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.tenhalf;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.FightConstant;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月27日 上午10:38:37
 */
@Component
public class CardCompater implements CardComparator {
	@Autowired
	CardTypeGetter cardTypeGetter;

	/***
	 * 庄家大时返回true
	 * 
	 * @param zhuangCards
	 * @param xianCards
	 * @return
	 */
	@Override
	public boolean compare(List<Integer> zhuangCards, List<Integer> xianCards) {
		TenHalfCardTypeConfig zhuangCardType = cardTypeGetter.get(zhuangCards);
		System.out.println("庄家牌型  :" + zhuangCardType.cardType);
		TenHalfCardTypeConfig xianCardType = cardTypeGetter.get(xianCards);
		System.out.println("闲家牌型  :" + xianCardType.cardType);
		int zhuangId = zhuangCardType.id;
		int xianId = xianCardType.id;

		if (zhuangId == xianId) {
			// 都是爆牌或牌一样多 庄家赢
			if (zhuangCards.size() == xianCards.size() || zhuangId == FightConstant.CARD_TYPE_BAO_PAI) {
				return true;
			} else {
				return zhuangCards.size() - xianCards.size() > 0;
			}
		} else {
			return zhuangId - xianId < 0;
		}
	}
}
