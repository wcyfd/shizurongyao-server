package com.randioo.shizurongyao_server.module.pillage.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.pillage.service.PillageService;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageRoleRequest;
@Controller
@PTAnnotation(PillageRoleRequest.class)
public class PillageRoleAction implements IActionSupport {
	@Autowired
	private PillageService pillageService;

	@Override
	public void execute(Object data, IoSession session) {
		PillageRoleRequest request = (PillageRoleRequest) data;
		Role role = (Role)RoleCache.getRoleBySession(session);
		pillageService.choose(role, request.getNpc(), session);
	}
}
