package com.randioo.shizurongyao_server.module.ping.service;

import org.apache.mina.core.session.IoSession;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.protocol.Ping.PingResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;

@Service("pingService")
public class PingServiceImpl extends ObserveBaseService implements PingService {

	@Override
	public GeneratedMessage ping(IoSession session, long clientTimestamp) {
		return SCMessage
				.newBuilder()
				.setPingResponse(
						PingResponse.newBuilder().setClientTimestamp(clientTimestamp)
								.setServerTimestamp(System.currentTimeMillis())).build();
	}

}
