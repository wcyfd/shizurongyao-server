package com.randioo.shizurongyao_server.entity.po;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.bo.Role;

public class FightVideo {
	private String videoId;
	private Map<Role, Byte> resultMap;
	private Map<Integer, Integer> scoreMap;
	private List<FightEvent> list = null;
	private Map<Role, String> legionNameMap = new HashMap<>();
	private Map<Role, Integer> lvMap = new HashMap<>();
	private Map<Role, Integer> pointMap = new HashMap<>();
	private Map<Role, Byte> sideMap = new HashMap<>();
	private Map<Role, String> cardListStrMap = new HashMap<>();
	private Map<Role,String> mainCardStrMap = new HashMap<>();
	private int endTime;

	public void setList(List<FightEvent> list) {
		this.list = list;
	}

	public List<FightEvent> getList() {
		return list;
	}

	public void setResultMap(Map<Role, Byte> resultMap) {
		this.resultMap = resultMap;
	}

	public Map<Role, Byte> getResultMap() {
		return resultMap;
	}

	public String getVideoId() {
		return videoId;
	}

	public void setVideoId(String videoId) {
		this.videoId = videoId;
	}

	public void setScoreMap(Map<Integer, Integer> scoreMap) {
		this.scoreMap = scoreMap;
	}

	public Map<Integer, Integer> getScoreMap() {
		return scoreMap;
	}

	public Map<Role, String> getLegionNameMap() {
		return legionNameMap;
	}

	public Map<Role, Integer> getLvMap() {
		return lvMap;
	}

	public Map<Role, Integer> getPointMap() {
		return pointMap;
	}

	public Map<Role, Byte> getSideMap() {
		return sideMap;
	}

	public Map<Role, String> getCardListStrMap() {
		return cardListStrMap;
	}
	
	public Map<Role, String> getMainCardStrMap() {
		return mainCardStrMap;
	}

	public int getEndTime() {
		return endTime;
	}

	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

}
