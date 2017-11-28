package com.randioo.compare_collections_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.compare_collections_server.entity.file.SpecialCardConfig;

public class SpecialCardConfigCache {

	private static Map<Integer, SpecialCardConfig> specialCardMap = new HashMap<>();

	public static void putConfig(SpecialCardConfig config) {
		specialCardMap.put(config.id, config);
	}

	public static Map<Integer, SpecialCardConfig> getSpecialCardMap() {
		return specialCardMap;
	}

}
