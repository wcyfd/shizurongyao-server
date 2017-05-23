package com.randioo.shizurongyao_server.module.gm.action;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.randioo_server_base.annotation.PTStringAnnotation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.shizurongyao_server.module.gm.service.GmService;

@Controller
@PTStringAnnotation("g11")
public class Gm11RankRefreshAction implements IActionSupport {
	@Autowired
	private GmService gmService;

	@Override
	public void execute(Object data, IoSession session) {
		gmService.rankRefresh();
	}
}
