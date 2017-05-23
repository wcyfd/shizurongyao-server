package com.randioo.shizurongyao_server.cache.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.WarBuildConfig;

public class WarBuildConfigCache {

	private static Map<Integer, WarBuildConfig> map = new HashMap<>();
	private static Map<Integer, List<Integer>> chapterBuildMap = new HashMap<>();

	public static void putConfig(WarBuildConfig config) {
		// TODO Auto-generated method stub
		map.put(config.buildId, config);
		List<Integer> list = chapterBuildMap.get(config.chapterId);
		if (list == null) {
			list = new ArrayList<>();
			chapterBuildMap.put(config.chapterId, list);
		}

		list.add(config.buildId);

	}

	public static WarBuildConfig getWarBuildConfigByBuildId(int buildId) {
		return map.get(buildId);
	}

	public static List<Integer> getBuildIdListByChapterId(int chapterId) {
		return chapterBuildMap.get(chapterId);
	}

}
