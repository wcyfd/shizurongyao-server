package com.randioo.shizurongyao_server.entity.po;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.shizurongyao_server.entity.bo.ClanMember;

public class Clan {

	private int clanId;
	private Map<Integer, ClanMember> memberMap = new ConcurrentHashMap<>();

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		this.clanId = clanId;
	}

	public Map<Integer, ClanMember> getMemberMap() {
		return memberMap;
	}

}
