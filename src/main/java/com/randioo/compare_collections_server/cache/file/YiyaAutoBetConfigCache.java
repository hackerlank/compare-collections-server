package com.randioo.compare_collections_server.cache.file;

import com.randioo.compare_collections_server.entity.file.YiyaAutoBetConfig;

import java.util.HashMap;
import java.util.Map;

/**
 * 金币场底注
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
public class YiyaAutoBetConfigCache {
	private static Map<Integer, YiyaAutoBetConfig> yiYaAutoBetMap = new HashMap<>();

	public static void putConfig(YiyaAutoBetConfig config) {
		yiYaAutoBetMap.put(config.id, config);
	}

	public static Map<Integer, YiyaAutoBetConfig> getYiYaAutoBetMap() {
		return yiYaAutoBetMap;
	}
}
