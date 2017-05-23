package com.randioo.shizurongyao_server.entity.po;

import java.util.HashMap;
import java.util.Map;

import com.randioo.randioo_server_base.module.match.MatchInfo;


public class OwlofwarMatchInfo extends MatchInfo {
	private Map<Integer, FightEventListener> fightEventListeners = new HashMap<>();
	private boolean isAIGame;
	private int aiMapsId;

	public Map<Integer, FightEventListener> getFightEventListeners() {
		return fightEventListeners;
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
}
