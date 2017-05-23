package com.randioo.shizurongyao_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.ExtraCardConfig;

public class ExtraCardConfigCache {

	private static Map<Integer, ExtraCardConfig> map = new HashMap<>();

	public static void putConfig(ExtraCardConfig config) {
		map.put(config.id, config);
	}

	public static Map<Integer, ExtraCardConfig> getExtraCardMap() {
		return map;
	}
}
