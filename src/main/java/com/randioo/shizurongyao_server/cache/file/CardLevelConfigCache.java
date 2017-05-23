package com.randioo.shizurongyao_server.cache.file;

import java.util.HashMap;
import java.util.Map;

import com.randioo.randioo_server_base.utils.GameUtils;
import com.randioo.shizurongyao_server.entity.file.CardLevelConfig;

public class CardLevelConfigCache {

	private static Map<Integer, Map<Integer, CardLevelConfig>> map = new HashMap<>();

	public static void putConfig(CardLevelConfig config) {
		GameUtils.mapABCInsert(map, config.quality, config.level, config);
	}

	public static CardLevelConfig getByQualityAndLevel(int quality, int level) {
		return GameUtils.mapGetValue(map, quality, level);
	}

}
