package com.randioo.shizurongyao_server.module.war.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.war.service.WarService;
import com.randioo.shizurongyao_server.protocol.War.WarShowWarChapterRequest;

@Controller
@PTAnnotation(WarShowWarChapterRequest.class)
public class WarShowWarChapterInfoAction implements IActionSupport {
	@Autowired
	private WarService warService;

	@Override
	public void execute(Object data, IoSession session) {
		WarShowWarChapterRequest request = (WarShowWarChapterRequest) data;

		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage message = warService.showWarChapterInfo(role);
		if (message != null) {
			session.write(message);
		}
	}
}
