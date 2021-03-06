package com.randioo.shizurongyao_server.module.market.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.market.service.MarketService;
import com.randioo.shizurongyao_server.protocol.Market.MarketArtificialRefreshRequest;

@Controller
@PTAnnotation(MarketArtificialRefreshRequest.class)
public class MarketArtificialRefreshMarketItemAction implements IActionSupport {
	@Autowired
	private MarketService marketService;

	@Override
	public void execute(Object data, IoSession session) {
		MarketArtificialRefreshRequest request = (MarketArtificialRefreshRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage message = marketService.artificalRefreshMarketItem(role);
		if (message != null) {
			session.write(message);
		}
	}
}
