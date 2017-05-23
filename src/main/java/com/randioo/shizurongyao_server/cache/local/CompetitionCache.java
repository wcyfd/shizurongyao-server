package com.randioo.shizurongyao_server.cache.local;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CompetitionCache {
	private static Map<Integer, Set<Integer>> competitionMap = new ConcurrentHashMap<>();

	public static Map<Integer, Set<Integer>> getCompetitionMap() {
		return competitionMap;
	}

	public static Set<Integer> getCompetitionSetById(int id) {
		return competitionMap.get(id);
	}
}
