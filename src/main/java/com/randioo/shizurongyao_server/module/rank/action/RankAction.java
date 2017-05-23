package com.randioo.shizurongyao_server.module.rank.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.rank.service.RankService;
import com.randioo.shizurongyao_server.protocol.Rank.RankShowClanPointRankRequest;



@Controller
@PTAnnotation(RankShowClanPointRankRequest.class)
public class RankAction implements IActionSupport{
	
	@Autowired
	private RankService rankService;

	@Override
	public void execute(Object data, IoSession session) {
		RankShowClanPointRankRequest request = (RankShowClanPointRankRequest)data;
		
		Role role = (Role)RoleCache.getRoleBySession(session);
		GeneratedMessage sc = rankService.showRank(role,request.getClanId());
		if (sc != null) {
			session.write(sc);
		}		
	}
	

}
