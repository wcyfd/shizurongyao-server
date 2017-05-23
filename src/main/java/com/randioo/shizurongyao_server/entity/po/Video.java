package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.protocol.Entity.Frame;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightLoadResource;

public class Video {
	private int gameId;
	/** 游戏帧 */
	private List<Frame> frames = new ArrayList<>();
	/** 资源信息表 */
	private List<SCFightLoadResource> roleResourceInfoMap = new ArrayList<>();
	/** 游戏结局 */
	private Map<Integer, GameResult> gameResultMap = new HashMap<>();
	
	private long startTime;

	public List<Frame> getFrames() {
		return frames;
	}

	public List<SCFightLoadResource> getRoleResourceInfoMap() {
		return roleResourceInfoMap;
	}

	public int getGameId() {
		return gameId;
	}

	public void setGameId(int gameId) {
		this.gameId = gameId;
	}

	public Map<Integer, GameResult> getGameResultMap() {
		return gameResultMap;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

}
