package com.randioo.shizurongyao_server.quartz;

import java.util.List;

import org.apache.mina.core.session.IoSession;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.shizurongyao_server.cache.local.RaceCache;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.Race.SCRaceEnter;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.protocol.War.WarMarchResponse;

public class RoleEntranceJob implements Job{

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		List <Integer>list  = RaceCache.getRoleRace().get(RaceCache.getRaceSize());
		for(int i = 0 ;i<list.size();i++){
			IoSession session = SessionCache.getSessionById(list.get(i));
			if (session != null) {
				//推送入场
				//session.write();
				session.write(SCMessage.newBuilder().setSCRaceEnter(SCRaceEnter.newBuilder()).build());
			}
		}
	}

}
