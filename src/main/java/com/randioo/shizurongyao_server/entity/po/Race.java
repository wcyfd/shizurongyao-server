package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.shizurongyao_server.protocol.Entity.RaceState;
import com.randioo.shizurongyao_server.protocol.Entity.RaceType;

/**
 * 比赛
 * 
 * @author wcy 2017年5月15日
 *
 */
public class Race {
	private int raceId;
	// 所有参赛选手，在开始后就定死了，不会变
	private Set<Integer> roleIdSet = new HashSet<>();
	// 比赛状态
	private RaceState raceState;
	// 比赛类型
	private RaceType raceType;
	// 最大报名人数
	private int maxSignUp;
	// 车轮战参赛的人数
	private int wheelCount;
	// 车轮战所在的局数,不会变
	private int wheelRound;
	// 当前淘汰赛局数
	private int currentRound;
	// 淘汰赛参赛选手情况表
	private Map<Integer, Map<Integer, Boolean>> roundRoleIdMap = new HashMap<>();
	// 下一轮淘汰赛开始时间
	private int nextRoundStartTime;
	// 下一轮车轮战开始时间
	private int nextWheelStartTime;
	// 车轮战参赛玩家id
	private List<Integer> wheelRoleIdList = new ArrayList<>();
	// 车轮战参赛玩家未比过的分组，使用数组下标
	private List<List<Integer>> wheelRoleIdIndexList = new ArrayList<>();
	// 当前一轮车轮战的玩家id
	private List<List<Integer>> wheelCurrentRoleIdList = new ArrayList<>();
	// 当前积分赛所在第几局
	private int wheelCurrentCount;

	public int getRaceId() {
		return raceId;
	}

	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	public int getMaxSignUp() {
		return maxSignUp;
	}

	public void setMaxSignUp(int maxSignUp) {
		this.maxSignUp = maxSignUp;
	}

	public Set<Integer> getRoleIdSet() {
		return roleIdSet;
	}

	public RaceState getRaceState() {
		return raceState;
	}

	public void setRaceState(RaceState raceState) {
		this.raceState = raceState;
	}

	public int getCurrentRound() {
		return currentRound;
	}

	public void setCurrentRound(int currentRound) {
		this.currentRound = currentRound;
	}

	public Map<Integer, Map<Integer, Boolean>> getRoundRoleIdMap() {
		return roundRoleIdMap;
	}

	public RaceType getRaceType() {
		return raceType;
	}

	public void setRaceType(RaceType raceType) {
		this.raceType = raceType;
	}

	public int getWheelCount() {
		return wheelCount;
	}

	public void setWheelCount(int wheelCount) {
		this.wheelCount = wheelCount;
	}

	public int getWheelRound() {
		return wheelRound;
	}

	public void setWheelRound(int wheelRound) {
		this.wheelRound = wheelRound;
	}

	public int getNextRoundStartTime() {
		return nextRoundStartTime;
	}

	public void setNextRoundStartTime(int nextRoundStartTime) {
		this.nextRoundStartTime = nextRoundStartTime;
	}

	public List<List<Integer>> getWheelRoleIdIndexList() {
		return wheelRoleIdIndexList;
	}

	public List<Integer> getWheelRoleIdList() {
		return wheelRoleIdList;
	}

	public List<List<Integer>> getWheelCurrentRoleIdList() {
		return wheelCurrentRoleIdList;
	}

	public int getWheelCurrentCount() {
		return wheelCurrentCount;
	}

	public void setWheelCurrentCount(int wheelCurrentCount) {
		this.wheelCurrentCount = wheelCurrentCount;
	}

	public int getNextWheelStartTime() {
		return nextWheelStartTime;
	}

	public void setNextWheelStartTime(int nextWheelStartTime) {
		this.nextWheelStartTime = nextWheelStartTime;
	}

}
