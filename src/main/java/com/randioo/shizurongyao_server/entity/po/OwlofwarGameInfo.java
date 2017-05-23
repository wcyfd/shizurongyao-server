package com.randioo.shizurongyao_server.entity.po;

import java.util.HashMap;
import java.util.Map;

public class OwlofwarGameInfo {
	private int roleId;
	private FightEventListener listener;
	private Map<Integer, Integer> scoreMap = new HashMap<>();
	private int countDown;

	public void setListener(FightEventListener listener) {
		this.listener = listener;
	}

	public FightEventListener getListener() {
		return listener;
	}

	public Map<Integer, Integer> getScoreMap() {
		return scoreMap;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getCountDown() {
		return countDown;
	}

	public void setCountDown(int countDown) {
		this.countDown = countDown;
	}

}
