package com.randioo.shizurongyao_server.entity.po;

import com.randioo.randioo_server_base.module.match.MatchRule;

public class OwlofwarMatchRule extends MatchRule {
	private FightEventListener fightEventListener;

	public FightEventListener getFightEventListener() {
		return fightEventListener;
	}

	public void setFightEventListener(FightEventListener fightEventListener) {
		this.fightEventListener = fightEventListener;
	}

}
