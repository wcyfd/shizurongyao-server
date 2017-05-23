package com.randioo.shizurongyao_server.cache.file;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.file.NPCPlayerConfig;

public class NPCPlayerConfigCache {

	private static Map<Integer, List<NPCPlayerConfig>> map = new HashMap<>();

	public static void putConfig(NPCPlayerConfig config) {
		List<NPCPlayerConfig> list = map.get(config.teamId);
		if (list == null) {
			list = new ArrayList<>();
			map.put(config.teamId, list);
		}
		list.add(config);
	}

	public static Map<Integer, List<NPCPlayerConfig>> getNPCPlayerMap() {
		return map;
	}

}
