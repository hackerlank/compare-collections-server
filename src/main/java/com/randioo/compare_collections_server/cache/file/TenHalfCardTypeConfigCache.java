/**
 *
 */
package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月25日 下午3:55:49
 */
public class TenHalfCardTypeConfigCache {
    /**
     * 牌型id  map
     */
    private static Map<Integer, TenHalfCardTypeConfig> TenHalfCardMapById = new HashMap<>();
    /**
     * 牌型数值 map
     */
    private static Map<Integer, TenHalfCardTypeConfig> TenHalfCardMapByPoint = new HashMap<>();

    public static void putConfig(TenHalfCardTypeConfig config) {
        TenHalfCardMapById.put(config.id, config);
        TenHalfCardMapByPoint.put(config.number, config);
    }

    public static TenHalfCardTypeConfig getCardTypeById(int id) {
        return TenHalfCardMapById.get(id);
    }

    public static TenHalfCardTypeConfig getCardTypeByPoint(int number) {
        return TenHalfCardMapByPoint.get(number);
    }

}
