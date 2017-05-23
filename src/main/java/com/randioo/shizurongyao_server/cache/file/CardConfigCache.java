package com.randioo.shizurongyao_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.CardConfig;

public class CardConfigCache {

	private static Map<Integer, CardConfig> map = new HashMap<>();

	public static void putConfig(CardConfig config) {
		if (config.canUse == 1)
			map.put(config.cardId, config);
	}

	public static Map<Integer, CardConfig> getCardConfigs() {
		return map;
	}

	public static CardConfig getConfigById(int cardId) {
		return map.get(cardId);
	}
}
