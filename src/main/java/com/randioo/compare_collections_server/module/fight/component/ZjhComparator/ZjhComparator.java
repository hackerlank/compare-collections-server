package com.randioo.compare_collections_server.module.fight.component.ZjhComparator;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.cache.file.ZJHCardConfigCache;
import com.randioo.compare_collections_server.entity.file.ZJHCardConfig;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

/**
 * 扎金花牌型大小比较器
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhComparator implements Comparator<RoleGameInfo> {
	@Override
	public int compare(RoleGameInfo o1, RoleGameInfo o2) {
		// 得到组牌的牌型
		Type o1Type = CardsType.type(Utils.getNum(o1.cards), Utils.getColor(o1.cards));
		Type o2Type = CardsType.type(Utils.getNum(o2.cards), Utils.getColor(o2.cards));
		if (o1Type.equals(Type.BAOZI) && o2Type.equals(Type.TESHU)
				|| o2Type.equals(Type.BAOZI) && o1Type.equals(Type.TESHU)) {
			if (Utils.getTypQuan(o1Type) > Utils.getTypQuan(o2Type)) {
				return -1;
			} else if (Utils.getTypQuan(o1Type) < Utils.getTypQuan(o2Type)) {
				return 1;
			}
		}

		if (Utils.getTypQuan(o1Type) > Utils.getTypQuan(o2Type) && Utils.getTypQuan(o2Type) == -1
				&& Utils.getTypQuan(o1Type) != 6) {
			return 1;
		} else if (Utils.getTypQuan(o1Type) == Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) == -1
				&& Utils.getTypQuan(o2Type) == -1) {
			// 都是特殊牌
			if (Utils.getPoint(o1.cards).get(2) > Utils.getPoint(o2.cards).get(2)) {
				return 1;
			} else if (Utils.getPoint(o1.cards).get(2) == Utils.getPoint(o2.cards).get(2)) {
				if (Utils.getPoint(o1.cards).get(1) > Utils.getPoint(o2.cards).get(1)) {
					return 1;
				} else if (Utils.getPoint(o1.cards).get(1) == Utils.getPoint(o2.cards).get(1)) {
					if (Utils.getPoint(o1.cards).get(0) > Utils.getPoint(o2.cards).get(0)) {
						return 1;
					} else if (Utils.getPoint(o1.cards).get(0) == Utils.getPoint(o2.cards).get(0)) {
						if (Utils.getColor(o1.cards).get(2) < Utils.getColor(o2.cards).get(2)) {
							return 1;
						} else {
							return -1;
						}
					} else {
						return -1;
					}
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else if (Utils.getTypQuan(o1Type) < Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) == -1
				&& Utils.getTypQuan(o2Type) != 6) {
			return -1;
		}

		// 获得牌权值
		if (Utils.getTypQuan(o1Type) > Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) != -1
				&& Utils.getTypQuan(o2Type) != -1) {// 权值大就大,特殊牌需要特殊处理
			return 1;
			// 对子和散牌和特殊牌不进来
		} else if (Utils.getTypQuan(o1Type) == Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) != 2
				&& Utils.getTypQuan(o2Type) != 2 && Utils.getTypQuan(o1Type) != 1 && Utils.getTypQuan(o2Type) != 1) {// 如果权值一样大，除了对子以外
			if (Utils.getPoint(o1.cards).get(2) > Utils.getPoint(o2.cards).get(2)) {// 拿牌组的最大的数字去比较，如果大就大
				return 1;
			} else if (Utils.getPoint(o1.cards).get(2) == Utils.getPoint(o2.cards).get(2)) {// 如果最大牌的数字也是一样大，那么比较花色
				if (Utils.getColor(o1.cards).get(2) < Utils.getColor(o2.cards).get(2)) {// 比花色，黑桃1，红桃2，草花3，放片4
					return 1;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
			// 是对子的情况
		} else if (Utils.getTypQuan(o1Type) == Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) == 2
				&& Utils.getTypQuan(o2Type) == 2) {
			// 对子牌重新排序
			List<Integer> o1cards = Utils.getNum(o1.cards);
			List<Integer> o2cards = Utils.getNum(o2.cards);
			if (o1cards.get(0) == o1cards.get(1)) {// 如果前两张一样，则第三张移到第一位
				Collections.reverse(o1cards);
			}
			if (o2cards.get(0) == o2cards.get(1)) {// 如果前两张一样，则第三张移到第一位
				Collections.reverse(o2cards);
			}
			Map<Integer, ZJHCardConfig> cardMap = ZJHCardConfigCache.getZJHCardMap();
			ZJHCardConfig o1card = cardMap.get(o1cards.get(2));
			ZJHCardConfig o2card = cardMap.get(o2cards.get(2));
			if (o1card.num > o2card.num) {// 对子的数字，大就大
				return 1;
			} else if (o1card.num == o2card.num) {// 数字一样的话
				if (o1card.color < o2card.color) {// 花色大就大
					return 1;
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else if (Utils.getTypQuan(o1Type) == Utils.getTypQuan(o2Type) && Utils.getTypQuan(o1Type) == 1
				&& Utils.getTypQuan(o2Type) == 1) {
			// 散牌
			if (Utils.getPoint(o1.cards).get(2) > Utils.getPoint(o2.cards).get(2)) {
				return 1;
			} else if (Utils.getPoint(o1.cards).get(2) == Utils.getPoint(o2.cards).get(2)) {
				if (Utils.getPoint(o1.cards).get(1) > Utils.getPoint(o2.cards).get(1)) {
					return 1;
				} else if (Utils.getPoint(o1.cards).get(1) == Utils.getPoint(o2.cards).get(1)) {
					if (Utils.getPoint(o1.cards).get(0) > Utils.getPoint(o2.cards).get(0)) {
						return 1;
					} else if (Utils.getPoint(o1.cards).get(0) == Utils.getPoint(o2.cards).get(0)) {
						if (Utils.getColor(o1.cards).get(2) < Utils.getColor(o2.cards).get(2)) {
							return 1;
						} else {
							return -1;
						}
					} else {
						return -1;
					}
				} else {
					return -1;
				}
			} else {
				return -1;
			}
		} else {
			return -1;
		}
	}
}
