package com.randioo.shizurongyao_server.scheduler;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.randioo_server_base.utils.scheduler.SchedulerInterface;
import com.randioo.shizurongyao_server.module.rank.service.RankService;

@Component
public class PointRankScheduler implements SchedulerInterface {

	private ScheduledExecutorService scheduledPool = Executors.newScheduledThreadPool(1);

	@Autowired
	private RankService rankService;

	@Override
	public void start() {
		scheduledPool.scheduleAtFixedRate(new Runnable() {

			@Override
			public void run() {
				rankService.refreshPointRank();

			}
		}, 0, 1, TimeUnit.SECONDS);

	}

	@Override
	public void shutdown(long timeout, TimeUnit unit) throws Exception {
		scheduledPool.shutdown();
		System.out.println("RoleDataScheduler wait shutdown");
		while (!scheduledPool.awaitTermination(timeout, unit)) {

		}
		System.out.println("RoleDataScheduler success shutdown");

	}

}
