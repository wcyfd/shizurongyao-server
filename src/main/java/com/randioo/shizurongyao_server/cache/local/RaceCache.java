package com.randioo.shizurongyao_server.cache.local;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.shizurongyao_server.entity.po.Race;

public class RaceCache {
	private static Set<Integer> roleIdSet = new HashSet<>();

	private static Integer raceSize = 32; // 比赛开始时的规模
	private static Map<Integer, List<Integer>> roleRace = new HashMap<>();
	private static Map<Integer, Integer> result = new HashMap<>();
	private static Map<Integer, Integer> award = new HashMap<>();

	public static Set<Integer> getRoleIdSet() {
		return roleIdSet;
	}

	public static Map<Integer, Integer> getResult() {
		return result;
	}

	public static Map<Integer, List<Integer>> getRoleRace() {
		return roleRace;
	}

	public static Integer getRaceSize() {
		return raceSize;
	}

	public static Map<Integer, Integer> getAward() {
		return award;
	}

	private static Map<Integer, Race> raceMap = new HashMap<>();

	public static Map<Integer, Race> getRaceMap() {
		return raceMap;
	}
}
