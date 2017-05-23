package com.randioo.shizurongyao_server.util;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.entity.RoleInterface;

public class SessionUtils {
	public static void sc(IoSession session, GeneratedMessage message) {
		if (session != null) {
			session.write(message);
		}
	}

	public static void sc(int roleId, GeneratedMessage message) {
		IoSession session = SessionCache.getSessionById(roleId);
		sc(session, message);
	}

	public static void sc(RoleInterface roleInterface, GeneratedMessage message) {
		sc(roleInterface.getRoleId(), message);
	}

}
