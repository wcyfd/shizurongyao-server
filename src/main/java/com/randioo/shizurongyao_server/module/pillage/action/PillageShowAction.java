package com.randioo.shizurongyao_server.module.pillage.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.pillage.service.PillageService;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageShowRequest;

@Controller
@PTAnnotation(PillageShowRequest.class)
public class PillageShowAction implements IActionSupport {
	@Autowired
	private PillageService pillageService;

	@Override
	public void execute(Object data, IoSession session) {
		PillageShowRequest request = (PillageShowRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage message = pillageService.showMatchingInfo(role);
		if (message != null)
			session.write(message);
	}
}
