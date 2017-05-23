package com.randioo.shizurongyao_server.entity.po;

import com.randioo.randioo_server_base.utils.scheduler.TimeEvent;
import com.randioo.shizurongyao_server.cache.local.RaceCache;

public abstract class RaceRoundTimeUp implements TimeEvent {

	private int raceId;
	private int endTime;
	private int targetRound;

	public void setRaceId(int raceId) {
		this.raceId = raceId;
	}

	public int getRaceId() {
		return raceId;
	}

	public int getTargetRound() {
		return targetRound;
	}

	public void setTargetRound(int targetRound) {
		this.targetRound = targetRound;
	}

	@Override
	public int getEndTime() {
		return endTime;
	}

	@Override
	public void setEndTime(int endTime) {
		this.endTime = endTime;
	}

	@Override
	public void update(TimeEvent timeEvent) {
		Race race = RaceCache.getRaceMap().get(raceId);
		// 如果时间到达时,局数还没有话,则进入下一轮
		if (race.getCurrentRound() != targetRound) {
			return;
		}
		
		nextRound(raceId);
	}

	public abstract void nextRound(int raceId);

}
