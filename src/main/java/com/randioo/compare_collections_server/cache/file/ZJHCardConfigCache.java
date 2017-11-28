package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.ZJHCardConfig;

public class ZJHCardConfigCache {

    private static Map<Integer, ZJHCardConfig> zjhCardMap = new HashMap<>();

    public static void putConfig(ZJHCardConfig config) {
        zjhCardMap.put(config.id, config);
    }

    public static Map<Integer, ZJHCardConfig> getZJHCardMap() {
        return zjhCardMap;
    }
}
