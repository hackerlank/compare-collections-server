/**
 * 
 */
package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.TenHalfCardConfig;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月25日 下午5:00:08
 */
public class TenHalfCardConfigCache {
    private static Map<Integer, TenHalfCardConfig> TenHalfCardMap = new HashMap<>();

    public static void putConfig(TenHalfCardConfig config) {
        TenHalfCardMap.put(config.id, config);
    }

    public static Map<Integer, TenHalfCardConfig> getTenHalfCardMap() {
        return TenHalfCardMap;
    }
}
