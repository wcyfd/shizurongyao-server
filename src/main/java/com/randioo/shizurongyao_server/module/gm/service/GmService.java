package com.randioo.shizurongyao_server.module.gm.service;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;

public interface GmService extends ObserveBaseServiceInterface{

	void loopSaveData(boolean mustSave);

	void command(String command,IoSession session);
	
	void money(Role role,int money);

	void god(Role role);

	void rankRefresh();
	
	void startMatch();

	void gameOver(Role role);
}
