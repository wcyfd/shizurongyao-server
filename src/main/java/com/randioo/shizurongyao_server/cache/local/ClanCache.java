package com.randioo.shizurongyao_server.cache.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.po.Clan;

public class ClanCache {

	private static Map<Integer, ClanMember> clanMemberMap = new ConcurrentHashMap<>();
	private static Map<Integer, Clan> clanMap = new ConcurrentHashMap<>();

	public static Map<Integer, ClanMember> getClanMemberMap() {
		return clanMemberMap;
	}

	public static Map<Integer, Clan> getClanMap() {
		return clanMap;
	}

}
