package com.randioo.shizurongyao_server.module.gm.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.module.gm.service.GmService;
import com.randioo.shizurongyao_server.protocol.Gm.GmCmdRequest;

@Controller
@PTAnnotation(GmCmdRequest.class)
public class GmCmdAction implements IActionSupport {
	@Autowired
	private GmService gmService;

	@Override
	public void execute(Object data, IoSession session) {
		GmCmdRequest request = (GmCmdRequest) data;
		gmService.command(request.getCmd(), session);

	}
}
