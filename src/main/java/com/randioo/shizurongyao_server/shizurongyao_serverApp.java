package com.randioo.shizurongyao_server;

import java.io.File;

import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.utils.ConfigLoader;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.system.GameServerInit;
import com.randioo.randioo_server_base.utils.system.GlobleConfig;
import com.randioo.randioo_server_base.utils.system.GlobleConfig.GlobleEnum;
import com.randioo.shizurongyao_server.quartz.QuartzManager;

/**
 * Hello world!
 *
 */
public class shizurongyao_serverApp {
	public static void main(String[] args) {
		StringUtils.printArgs(args);
		GlobleConfig.init(args);

		// 添加日志文件夹
		File file = new File("log");
		file.delete();
		file.mkdir();

		ConfigLoader.loadConfig("com.randioo.shizurongyao_server.entity.file", "./config.zip");
		SensitiveWordDictionary.readAll("./sensitive.txt");

		SpringContext.initSpringCtx("ApplicationContext.xml");

		((GameServerInit) SpringContext.getBean("gameServerInit")).setHandler(new ServerHandler()).start();
		QuartzManager manager = (QuartzManager)SpringContext.getBean("quartzManager");
		manager.openQuartz();
		
		GlobleConfig.set(GlobleEnum.LOGIN, true);

	}
}
