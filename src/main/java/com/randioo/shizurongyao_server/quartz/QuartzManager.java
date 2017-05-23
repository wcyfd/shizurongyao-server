package com.randioo.shizurongyao_server.quartz;

import org.quartz.Scheduler;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.stereotype.Component;

@Component("quartzManager")
public class QuartzManager implements InitializingBean {

	private SchedulerFactory schedulerFactory = new StdSchedulerFactory();

	private Scheduler scheduler;

	@Override
	public void afterPropertiesSet() throws Exception {
		scheduler = schedulerFactory.getScheduler();
	}

	public Scheduler getScheduler() {
		return scheduler;
	}

	public void openQuartz() {
		System.out.println("比赛计时器已启动！！！");
		try{
//		SchedulerFactory schedulerfactory = new StdSchedulerFactory();
//		Scheduler scheduler = null;
//
//		try {
//			scheduler = schedulerfactory.getScheduler();
//
//			// 比赛入场
//			JobDetail roleEntranceJob = JobBuilder.newJob(RoleEntranceJob.class)
//					.withIdentity("roleEntranceJob", "jgroup").build();
//			Trigger roleZeroTrigger = TriggerBuilder.newTrigger().withIdentity("roleEntranceTrigger", "triggerGroup")
//					.withSchedule(CronScheduleBuilder.cronSchedule("0 17 17 * * ?")).startNow().build();
//			scheduler.scheduleJob(roleEntranceJob, roleZeroTrigger);
//
//			// 比赛入场
//			JobDetail startRaceJob = JobBuilder.newJob(StartRaceJob.class).withIdentity("startRaceJob", "jgroup")
//					.build();
//			Trigger startRaceZeroTrigger = TriggerBuilder.newTrigger().withIdentity("startRaceTrigger", "triggerGroup")
//					.withSchedule(CronScheduleBuilder.cronSchedule("0 56 15 * * ?")).startNow().build();
//			scheduler.scheduleJob(startRaceJob, startRaceZeroTrigger);

			scheduler.start();

		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

}
