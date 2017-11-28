package com.randioo.compare_collections_server.module.fight.component.ZjhComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import com.randioo.compare_collections_server.cache.file.ZJHCardConfigCache;
import com.randioo.compare_collections_server.entity.file.ZJHCardConfig;

/**
 * 扎金花牌的花色数字工具
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
public class Utils {
	// 获取牌的牌面
	public static List<Integer> getNum(List<Integer> list) {
		List<Integer> num = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<Integer, ZJHCardConfig> cardMap = ZJHCardConfigCache.getZJHCardMap();
			ZJHCardConfig card = cardMap.get(list.get(i));
			num.add(card.cardNum);
		}
		Collections.sort(num);
		return num;
	}

	// 获取牌的花色
	public static List<Integer> getColor(List<Integer> list) {
		List<Integer> num = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<Integer, ZJHCardConfig> cardMap = ZJHCardConfigCache.getZJHCardMap();
			ZJHCardConfig card = cardMap.get(list.get(i));
			num.add(card.color);
		}
		Collections.sort(num);
		return num;
	}

	// 获取牌的数值
	public static List<Integer> getPoint(List<Integer> list) {
		List<Integer> point = new ArrayList<>();
		for (int i = 0; i < list.size(); i++) {
			Map<Integer, ZJHCardConfig> cardMap = ZJHCardConfigCache.getZJHCardMap();
			ZJHCardConfig card = cardMap.get(list.get(i));
			point.add(card.num);
		}
		Collections.sort(point);
		return point;
	}

	/**
	 * 获取牌型的权值
	 * 
	 * @param type
	 * @return
	 */
	public static int getTypQuan(Type type) {
		if (type == Type.BAOZI) {
			return 6;
		} else if (type == Type.TONGHUASHUN) {
			return 5;
		} else if (type == Type.TONGHUA) {
			return 4;
		} else if (type == Type.SHUNZI) {
			return 3;
		} else if (type == Type.DUIZI) {
			return 2;
		} else if (type == Type.SANPAI) {
			return 1;
		}
		return -1;
	}
}
