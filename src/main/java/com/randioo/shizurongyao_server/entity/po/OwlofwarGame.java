package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.RTSGame;
import com.randioo.shizurongyao_server.entity.bo.Role;

public class OwlofwarGame extends RTSGame {

	/** 游戏id */
	private int gameId;
	/** 玩家数量 */
	private int controlCount;
	/** 总时间 */
	private int totalTime;
	// /** 等待结果经历的秒数 */
	// private int waitResultSecond;
	// /** 是否已经得出结果 */
	// private boolean hasResult;
	/** 是否开始 */
	private boolean start;
	/** 是否结束 */
	private boolean end;
	/** 玩家Id列表 */
	private Map<Integer, Role> roleMap = new HashMap<>();
	/** 玩家顺序列表 */
	private List<Integer> rolePositionList = new ArrayList<>();

	private Map<Integer, OwlofwarGameInfo> roleGameInfoMap = new HashMap<>();
	/** 房间锁 */
	private ReentrantLock lock = new ReentrantLock();;
	/** 是否是aigame */
	private boolean isAIGame;
	/** 地图 */
	private int aiMapsId;

	/** 开始时间 */
	private long startTime;
	// /** 结束时间 */
	// private int endTime;
	/** 结果 */
	private Map<Integer, Byte> resultMap = new HashMap<>();
	/** 分数结果 */
	private Map<Integer, Integer> scoreMap = new HashMap<>();
	/** ai数量 */
	private int aiCount;

	// private int currentWaitResultTime = 0;
	/** 准备好的玩家id集合 */
	private Set<Integer> readyRoleIdSet = new HashSet<>();
	/** 资源加载完成的玩家id */
	private Set<Integer> resourceCompleteRoleIdSet = new HashSet<>();

	private boolean hasResult = false;

	public Set<Integer> getResourceCompleteRoleIdSet() {
		return resourceCompleteRoleIdSet;
	}

	// private int addDeltaFrame;
	private int frameCountInOneSecond;

	private Video video;

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public ReentrantLock getLock() {
		return lock;
	}

	public Map<Integer, Role> getRoleMap() {
		return roleMap;
	}

	public int getControlCount() {
		return controlCount;
	}

	public void setControlCount(int controlCount) {
		this.controlCount = controlCount;
	}

	public boolean isEnd() {
		return end;
	}

	public void setEnd(boolean end) {
		this.end = end;
		// if (this.end && endTime == 0) {
		// endTime = TimeUtils.getNowTime();
		// }
	}

	public int getTotalTime() {
		return totalTime;
	}

	public void setTotalTime(int totalTime) {
		this.totalTime = totalTime;
	}

	public boolean isStart() {
		return start;
	}

	public void setStart(boolean start) {
		this.start = start;
	}

	public List<Integer> getRolePositionList() {
		return rolePositionList;
	}

	public Map<Integer, OwlofwarGameInfo> getRoleGameInfoMap() {
		return roleGameInfoMap;
	}

	public boolean isAIGame() {
		return isAIGame;
	}

	public void setAIGame(boolean isAIGame) {
		this.isAIGame = isAIGame;
	}

	public int getAiMapsId() {
		return aiMapsId;
	}

	public void setAiMapsId(int aiMapsId) {
		this.aiMapsId = aiMapsId;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	// public boolean isHasResult() {
	// return hasResult;
	// }
	//
	// public void setHasResult(boolean hasResult) {
	// this.hasResult = hasResult;
	// }
	//
	// public int getWaitResultSecond() {
	// return waitResultSecond;
	// }
	//
	// public void setWaitResultSecond(int waitResultSecond) {
	// this.waitResultSecond = waitResultSecond;
	// }

	public Map<Integer, Byte> getResultMap() {
		return resultMap;
	}

	public Map<Integer, Integer> getScoreMap() {
		return scoreMap;
	}

	// public int getEndTime() {
	// return endTime;
	// }

	public Role getAnotherRole(Role role) {
		for (Role r : roleMap.values()) {
			if (role != r)
				return r;
		}
		return null;
	}

	public int getAiCount() {
		return aiCount;
	}

	public void setAiCount(int aiCount) {
		this.aiCount = aiCount;
	}

	// public int getCurrentWaitResultTime() {
	// return currentWaitResultTime;
	// }
	//
	// public void setCurrentWaitResultTime(int currentWaitResultTime) {
	// this.currentWaitResultTime = currentWaitResultTime;
	// }

	public Set<Integer> getReadyRoleIdSet() {
		return readyRoleIdSet;
	}

	public void setReadyRoleIdSet(Set<Integer> readyRoleIdSet) {
		this.readyRoleIdSet = readyRoleIdSet;
	}

	// public int getAddDeltaFrame() {
	// return addDeltaFrame;
	// }
	//
	public void setAddDeltaFrame(int addDeltaFrame) {
		this.addDeltaFrame = addDeltaFrame;
	}

	public int getFrameCountInOneSecond() {
		return frameCountInOneSecond;
	}

	public void setFrameCountInOneSecond(int frameCountInOneSecond) {
		this.frameCountInOneSecond = frameCountInOneSecond;
	}

	public void setVideo(Video video) {
		this.video = video;
	}

	public Video getVideo() {
		return video;
	}

	public boolean isHasResult() {
		return hasResult;
	}

	public void setHasResult(boolean hasResult) {
		this.hasResult = hasResult;
	}

}
