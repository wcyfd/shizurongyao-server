package com.randioo.shizurongyao_server.cache.local;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.po.PointRank;

public class RankCache {
	private static Map<Integer, List<PointRank>> pointRankMap = new HashMap<>();

	public static Map<Integer, List<PointRank>> pointRankMap() {
		return pointRankMap;
	}

	public static List<PointRank> getPointRankList(int clanId) {
		return pointRankMap.get(clanId);
	}

}
