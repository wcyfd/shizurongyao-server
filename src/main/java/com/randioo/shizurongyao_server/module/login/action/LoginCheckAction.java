package com.randioo.shizurongyao_server.module.login.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.module.login.service.LoginService;
import com.randioo.shizurongyao_server.protocol.Login.LoginCheckAccountRequest;

@Controller
@PTAnnotation(LoginCheckAccountRequest.class)
public class LoginCheckAction implements IActionSupport {

	@Autowired
	private LoginService loginService;

	@Override
	public void execute(Object data, IoSession session) {
		LoginCheckAccountRequest request = (LoginCheckAccountRequest) data;
		GeneratedMessage sc = loginService.login(request.getAccount());
		if (sc != null) {
			session.write(sc);
		}
	}

}
