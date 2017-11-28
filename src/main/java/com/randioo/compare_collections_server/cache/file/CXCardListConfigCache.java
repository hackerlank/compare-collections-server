package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.CXCardListConfig;

public class CXCardListConfigCache {

	private static Map<Integer, CXCardListConfig> cxCardListMap = new HashMap<>();

	public static void putConfig(CXCardListConfig config) {
		cxCardListMap.put(config.id, config);
	}

	public static Map<Integer, CXCardListConfig> getCxCardListMap() {
		return cxCardListMap;
	}
}
