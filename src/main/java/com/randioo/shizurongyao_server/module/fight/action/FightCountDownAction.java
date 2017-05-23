package com.randioo.shizurongyao_server.module.fight.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.fight.service.FightService;
import com.randioo.shizurongyao_server.protocol.Fight.FightCountDownRequest;

@Controller
@PTAnnotation(FightCountDownRequest.class)
public class FightCountDownAction implements IActionSupport {
	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, IoSession session) {
		FightCountDownRequest request = (FightCountDownRequest) data;
		Role role = (Role)RoleCache.getRoleBySession(session);
		fightService.gameCountDown(role, session);
	}
}
