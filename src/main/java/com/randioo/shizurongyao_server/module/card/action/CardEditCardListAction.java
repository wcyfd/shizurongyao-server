package com.randioo.shizurongyao_server.module.card.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.card.service.CardService;
import com.randioo.shizurongyao_server.protocol.Card.CardEditCardListRequest;

@Controller
@PTAnnotation(CardEditCardListRequest.class)
public class CardEditCardListAction implements IActionSupport {
	@Autowired
	private CardService cardService;

	@Override
	public void execute(Object data, IoSession session) {
		CardEditCardListRequest request = (CardEditCardListRequest) data;
		Role role = (Role) RoleCache.getRoleBySession(session);
		GeneratedMessage message = cardService.changeCards(role, request.getId(), request.getIndex(),
				request.getCardId());
		if (message != null) {
			session.write(message);
		}
	}
}
