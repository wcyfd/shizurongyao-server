package com.randioo.shizurongyao_server.module.pillage.service;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;

public interface PillageService extends ObserveBaseServiceInterface{
	public void choose(Role role, boolean isAI,IoSession session);

	public void cancelChoose(Role role,IoSession session);


	GeneratedMessage showMatchingInfo(Role role);

	void competitionNotice(Role role, int competitionId, IoSession session);

	public void practice(Role role,IoSession session);

}
