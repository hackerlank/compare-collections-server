/**
 *
 */
package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.ZJHCardTypeConfig;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月25日 下午4:00:20
 */
public class ZJHCardTypeConfigCache {
    private static Map<Integer, ZJHCardTypeConfig> zjhCardTypeMap = new HashMap<>();

    public static void putConfig(ZJHCardTypeConfig config) {
        zjhCardTypeMap.put(config.id, config);
    }

    public static Map<Integer, ZJHCardTypeConfig> getZJHCardListMap() {
        return zjhCardTypeMap;
    }
}
