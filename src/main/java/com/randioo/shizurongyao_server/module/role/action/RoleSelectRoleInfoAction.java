package com.randioo.shizurongyao_server.module.role.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.role.service.RoleService;
import com.randioo.shizurongyao_server.protocol.Role.RoleInfoSelectRequest;

@Controller
@PTAnnotation(RoleInfoSelectRequest.class)
public class RoleSelectRoleInfoAction implements IActionSupport {
	@Autowired
	private RoleService roleService;

	@Override
	public void execute(Object data, IoSession session) {
		RoleInfoSelectRequest request = (RoleInfoSelectRequest) data;

		Role role = (Role) RoleCache.getRoleBySession(session);
		roleService.selectRoleInfo(role, request.getRoleInfoTypeList());
	}

}
