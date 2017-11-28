package com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator;

import java.util.List;

import com.google.common.collect.Lists;
import com.randioo.compare_collections_server.cache.file.CXCardListConfigCache;
import com.randioo.compare_collections_server.cache.file.CXCardTypeConfigCache;
import com.randioo.compare_collections_server.entity.file.CXCardListConfig;
import com.randioo.compare_collections_server.entity.file.CXCardTypeConfig;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

public class CxUtils {
    /**
     * 是否包含牌型
     * 
     * @param twoCards
     * @return
     * @author wcy 2017年11月15日
     */
    public static CXCardTypeConfig getCardType(List<Integer> twoCards) {
        String cardType1 = twoCards.get(0) + ";" + twoCards.get(1);
        String cardType2 = twoCards.get(1) + ";" + twoCards.get(0);
        if (CXCardTypeConfigCache.getCxCardTypeMap().containsKey(cardType1)) {
            return CXCardTypeConfigCache.getCxCardTypeMap().get(cardType1);
        }
        if (CXCardTypeConfigCache.getCxCardTypeMap().containsKey(cardType2)) {
            return CXCardTypeConfigCache.getCxCardTypeMap().get(cardType2);
        }
        return null;
    }

    public static CXCardListConfig getCardValue(int value) {
        return CXCardListConfigCache.getCxCardListMap().get(value);
    }

    /**
     * 获得和的个位数的值
     * 
     * @param twoCards
     * @return
     * @author wcy 2017年11月16日
     */
    public static int getSumSingleValue(List<Integer> twoCards) {
        int num1 = CXCardListConfigCache.getCxCardListMap().get(twoCards.get(0)).num;
        int num2 = CXCardListConfigCache.getCxCardListMap().get(twoCards.get(1)).num;
        return (num1 + num2) % 10;
    }

    /**
     * 获得两张牌
     * 
     * @param role
     * @param position
     * @return
     * @author wcy 2017年11月16日
     */
    public static List<Integer> get2Cards(RoleGameInfo role, String position) {
        if ("head".equals(position)) {
            return Lists.newArrayList(role.cards.get(0), role.cards.get(1));
        } else if ("tail".equals(position)) {
            return Lists.newArrayList(role.cards.get(2), role.cards.get(3));
        }
        return null;
    }

    public static int forceCompareCards(List<Integer> part1, List<Integer> part2) {
        CXCardTypeConfig cardConfig1 = CxUtils.getCardType(part1);
        CXCardTypeConfig cardConfig2 = CxUtils.getCardType(part2);

        // 先查头牌有没有牌型，有牌型的比没牌型的大
        // 都有牌型的比点数
        if (cardConfig1 != null && cardConfig2 != null) {
            if (cardConfig1.num == cardConfig2.num) {// 比牌型
                return forceCompare(part1, part2);
            } else {
                return cardConfig1.num - cardConfig2.num;
            }
        } else if (cardConfig1 == null && cardConfig2 == null) {// 都没牌型的比和的个位数
            int num1 = CxUtils.getSumSingleValue(part1);
            int num2 = CxUtils.getSumSingleValue(part2);
            if (num1 == num2) {
                return forceCompare(part1, part2);
            } else {
                return num1 - num2;
            }
        } else {
            return cardConfig1 != null ? 1 : -1;
        }
    }
    
    public static int compareCards(List<Integer> part1, List<Integer> part2) {
        CXCardTypeConfig cardConfig1 = CxUtils.getCardType(part1);
        CXCardTypeConfig cardConfig2 = CxUtils.getCardType(part2);

        // 先查头牌有没有牌型，有牌型的比没牌型的大
        // 都有牌型的比点数
        if (cardConfig1 != null && cardConfig2 != null) {
            return cardConfig1.num - cardConfig2.num;
        } else if (cardConfig1 == null && cardConfig2 == null) {// 都没牌型的比和的个位数
            return CxUtils.getSumSingleValue(part1) - CxUtils.getSumSingleValue(part2);
        } else {
            return cardConfig1 != null ? 1 : -1;
        }
    }

    /**
     * 强制比较
     * 
     * @param twoCards1
     * @param twoCards2
     * @return
     * @author wcy 2017年11月16日
     */
    private static int forceCompare(List<Integer> twoCards1, List<Integer> twoCards2) {
        int num1_0 = CxUtils.getCardValue(twoCards1.get(0)).num;
        int num1_1 = CxUtils.getCardValue(twoCards1.get(1)).num;
        int num2_0 = CxUtils.getCardValue(twoCards2.get(0)).num;
        int num2_1 = CxUtils.getCardValue(twoCards2.get(1)).num;

        int maxCards1 = Math.max(num1_0, num1_1);
        int maxCards2 = Math.max(num2_0, num2_1);
        if (maxCards1 == maxCards2) {
            int minCards1 = Math.min(num1_0, num1_1);
            int minCards2 = Math.min(num2_0, num2_1);

            return minCards1 - minCards2;
        }
        return maxCards1 - maxCards2;
    }
}
