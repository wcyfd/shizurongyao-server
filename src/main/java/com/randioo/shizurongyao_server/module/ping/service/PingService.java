package com.randioo.shizurongyao_server.module.ping.service;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;

public interface PingService extends ObserveBaseServiceInterface{
	public GeneratedMessage ping(IoSession session,long clientTimestamp);
}
