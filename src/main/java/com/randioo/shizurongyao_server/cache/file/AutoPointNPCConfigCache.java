package com.randioo.shizurongyao_server.cache.file;

import java.util.LinkedHashMap;
import java.util.Map;

import com.randioo.randioo_server_base.utils.GameUtils;
import com.randioo.shizurongyao_server.entity.file.AutoPointNPCConfig;

/**
 * excel
 * 
 * @author wcy 2017年5月5日
 *
 */
public class AutoPointNPCConfigCache {
	private static Map<Integer, Map<Integer, AutoPointNPCConfig>> autoPointNPCMap = new LinkedHashMap<>();

	public static void putConfig(AutoPointNPCConfig config) {
		GameUtils.mapABCInsert(autoPointNPCMap, config.point, config.mapId, config);
	}

	public static Map<Integer, Map<Integer, AutoPointNPCConfig>> getAutoPointNPCMap() {
		return autoPointNPCMap;
	}
}
