package com.randioo.shizurongyao_server.module.clan.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.clan.service.ClanService;
import com.randioo.shizurongyao_server.protocol.Clan.ClanShowAllClanRequest;

@Controller
@PTAnnotation(ClanShowAllClanRequest.class)
public class ClanShowAllAction implements IActionSupport {

	@Autowired
	private ClanService clanService;

	@Override
	public void execute(Object data, IoSession session) {
		ClanShowAllClanRequest request = (ClanShowAllClanRequest) data;
		Role role = (Role)RoleCache.getRoleBySession(session);
		GeneratedMessage sc = clanService.showClan(role);
		if (sc != null) {
			session.write(sc);
		}
	}

}
