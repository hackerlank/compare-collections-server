package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.CXCardTypeConfig;

public class CXCardTypeConfigCache {

	private static Map<String, CXCardTypeConfig> cxCardTypeMap = new HashMap<>();

	public static void putConfig(CXCardTypeConfig config) {
		cxCardTypeMap.put(config.cardType, config);
	}

	public static Map<String, CXCardTypeConfig> getCxCardTypeMap() {
		return cxCardTypeMap;
	}

}
