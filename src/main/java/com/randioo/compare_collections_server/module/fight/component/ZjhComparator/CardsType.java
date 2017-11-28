package com.randioo.compare_collections_server.module.fight.component.ZjhComparator;

import java.util.List;

/**
 * 判断扑克牌类型
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
public class CardsType {
	public static Type type(List<Integer> cardsNum, List<Integer> color) {
		if (isBaozi(cardsNum, color)) {
			return Type.BAOZI;
		} else if (isTonghuashun(cardsNum, color)) {
			return Type.TONGHUASHUN;
		} else if (isTongHua(cardsNum, color)) {
			return Type.TONGHUA;
		} else if (isShunzi(cardsNum, color)) {
			return Type.SHUNZI;
		} else if (isDuizi(cardsNum, color)) {
			return Type.DUIZI;
		} else if (isSanPai(cardsNum, color)) {
			return Type.SANPAI;
		} else {
			return Type.TESHU;
		}
	}

	/**
	 * 判断是否是豹子(豹子要求3张牌面大小相同)
	 * 
	 * return
	 */
	private static boolean isBaozi(List<Integer> cardsNum, List<Integer> color) {
		for (int i = 0, size = cardsNum.size(); i < size; i++) {
			if (cardsNum.get(0) == cardsNum.get(1) && cardsNum.get(1) == cardsNum.get(2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是顺子
	 * 
	 * return
	 */
	private static boolean isShunzi(List<Integer> cardsNum, List<Integer> color) {
		for (int i = 0, size = cardsNum.size(); i < size; i++) {
			if (cardsNum.get(1) - cardsNum.get(0) == 1 && cardsNum.get(2) - cardsNum.get(1) == 1) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是同花顺
	 * 
	 * return
	 */
	private static boolean isTonghuashun(List<Integer> cardsNum, List<Integer> color) {
		if (!isShunzi(cardsNum, color)) {
			return false;
		}
		for (int i = 0, size = cardsNum.size(); i < size; i++) {
			if (color.get(0) == color.get(1) && color.get(1) == color.get(2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否是同花
	 * 
	 * return
	 */
	private static boolean isTongHua(List<Integer> cardsNum, List<Integer> color) {
		if (isShunzi(cardsNum, color)) {
			return false;
		}
		for (int i = 0, size = cardsNum.size(); i < size; i++) {
			if (color.get(0) == color.get(1) && color.get(1) == color.get(2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是对子
	 * 
	 * return
	 */
	private static boolean isDuizi(List<Integer> cardsNum, List<Integer> color) {
		for (int i = 0, size = cardsNum.size(); i < size; i++) {
			if (cardsNum.get(0) == cardsNum.get(1) || cardsNum.get(0) == cardsNum.get(2)
					|| cardsNum.get(1) == cardsNum.get(2)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否是散牌
	 * 
	 * return
	 */
	private static boolean isSanPai(List<Integer> cardsNum, List<Integer> color) {
		if (isShunzi(cardsNum, color) || isTongHua(cardsNum, color) || isDuizi(cardsNum, color)
				|| isBaozi(cardsNum, color) || isTonghuashun(cardsNum, color)) {
			return false;
		}
		if (cardsNum.get(0) != 2 || cardsNum.get(1) != 3 || cardsNum.get(2) != 5) {
			return true;
		}
		return false;
	}
}
