package com.randioo.shizurongyao_server.module.gm.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTStringAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.gm.service.GmService;

@Controller
@PTStringAnnotation("g01")
public class Gm01MoneyAction implements IActionSupport {
	@Autowired
	private GmService gmService;

	@Override
	public void execute(Object data, IoSession session) {
		Role role = (Role) RoleCache.getRoleBySession(session);
		int money = Integer.parseInt(((String) data).split(" ")[0]);
		gmService.money(role, money);

	}
}
