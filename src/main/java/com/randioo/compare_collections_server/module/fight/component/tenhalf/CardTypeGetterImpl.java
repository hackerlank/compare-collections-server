/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.tenhalf;

import com.randioo.compare_collections_server.cache.file.TenHalfCardConfigCache;
import com.randioo.compare_collections_server.cache.file.TenHalfCardTypeConfigCache;
import com.randioo.compare_collections_server.entity.file.TenHalfCardConfig;
import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.FightConstant;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月27日 上午10:26:28
 */
@Component
public class CardTypeGetterImpl implements CardTypeGetter {

	@Override
	public TenHalfCardTypeConfig get(List<Integer> cards) {
		// 点数总数
		int pointSum = 0;
		// 几张牌
		int cardCount = cards.size();
		TenHalfCardTypeConfig cardType = null;

		for (Integer card : cards) {
			TenHalfCardConfig cardConfig = TenHalfCardConfigCache.getTenHalfCardMap().get(card);
			pointSum += cardConfig.point;
		}

		if (pointSum > 105) {
			// 爆牌
			cardType = TenHalfCardTypeConfigCache.getCardTypeById(FightConstant.CARD_TYPE_BAO_PAI);
		} else {
			if (cardCount == 5) {
				if (pointSum == 25) {
					// 五花龙
					cardType = TenHalfCardTypeConfigCache.getCardTypeById(2);
				} else {
					if (pointSum == 105) {
						// 天龙
						cardType = TenHalfCardTypeConfigCache.getCardTypeById(1);
					} else {
						// 五龙
						cardType = TenHalfCardTypeConfigCache.getCardTypeById(3);
					}
				}
			} else {
				cardType = TenHalfCardTypeConfigCache.getCardTypeByPoint(pointSum);
			}
		}
        if (cardType == null) {
            cardType = TenHalfCardTypeConfigCache.getCardTypeById(FightConstant.CARD_TYPE_BAO_PAI);
        }
        return cardType;
	}
}
