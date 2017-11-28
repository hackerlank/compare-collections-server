package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.ZhuangTypeConfig;

public class ZhuangTypeConfigCache {

	private static Map<Integer, ZhuangTypeConfig> zhuangTypeMap = new HashMap<>();

	public static void putConfig(ZhuangTypeConfig config) {
		zhuangTypeMap.put(config.id, config);
	}

	public static Map<Integer, ZhuangTypeConfig> getZhuangTypeMap() {
		return zhuangTypeMap;
	}
}
