package com.randioo.shizurongyao_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.FamilyNameConfig;

public class FamilyNameConfigCache {

	private static Map<Integer, FamilyNameConfig> map = new HashMap<>();

	public static void putConfig(FamilyNameConfig config) {
		map.put(config.clanId, config);
	}

	public static Map<Integer, FamilyNameConfig> getFamilyNameMap() {
		return map;
	}

}
