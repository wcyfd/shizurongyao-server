package com.randioo.shizurongyao_server.module.match.service;

import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;

public interface MatchService extends ObserveBaseServiceInterface{
	public void matchRole(IoSession session,Role role, boolean isAI, FightEventListener listener);

	public void matchRole(Role role1, FightEventListener listener1, Role role2, FightEventListener listener2);

	void cancelMatch(Role role);
	
	void matchComplete(OwlofwarGame game);

	void init();

	public void offline(Role role);

}
