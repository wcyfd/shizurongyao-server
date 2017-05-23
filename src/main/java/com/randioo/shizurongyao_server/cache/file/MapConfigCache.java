package com.randioo.shizurongyao_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.MapConfig;

public class MapConfigCache {

	private static Map<Integer, MapConfig> map = new HashMap<>();

	public static void putConfig(MapConfig config) {
		map.put(config.mapId, config);
	}

	public static MapConfig getConfigById(int mapId) {
		return map.get(mapId);
	}

}
