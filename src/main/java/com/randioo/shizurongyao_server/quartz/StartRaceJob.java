package com.randioo.shizurongyao_server.quartz;

import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.shizurongyao_server.module.race.service.RaceService;

public class StartRaceJob implements Job {

	@Autowired
	private RaceService raceService;

	@Override
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		raceService.startRace(0);
	}

}
