package com.randioo.shizurongyao_server.module.race.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.utils.Observer;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.scheduler.EventScheduler;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.local.RaceCache;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.FightEventListenerAdapter;
import com.randioo.shizurongyao_server.entity.po.Race;
import com.randioo.shizurongyao_server.entity.po.RaceRoundTimeUp;
import com.randioo.shizurongyao_server.entity.po.RaceWheelTimeUp;
import com.randioo.shizurongyao_server.module.fight.FightConstant;
import com.randioo.shizurongyao_server.module.fight.FightConstant.GameFightType;
import com.randioo.shizurongyao_server.module.fight.service.FightService;
import com.randioo.shizurongyao_server.module.login.service.LoginService;
import com.randioo.shizurongyao_server.module.match.service.MatchService;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Entity.RaceState;
import com.randioo.shizurongyao_server.protocol.Entity.RaceType;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.Race.GetRaceRoleNumResponse;
import com.randioo.shizurongyao_server.protocol.Race.RaceCancelSignUpResponse;
import com.randioo.shizurongyao_server.protocol.Race.RaceDescResponse;
import com.randioo.shizurongyao_server.protocol.Race.RaceSignUpResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.quartz.QuartzManager;
import com.randioo.shizurongyao_server.quartz.RoleEntranceJob;
import com.randioo.shizurongyao_server.quartz.StartRaceJob;

@Service("raceService")
public class RaceServiceImpl extends ObserveBaseService implements RaceService {

	@Autowired
	private MatchService matchService;
	@Autowired
	private LoginService loginService;
	@Autowired
	private FightService fightService;
	@Autowired
	private QuartzManager quartzManager;

	@Autowired
	private EventScheduler eventScheduler;

	private static int target = 0;
	private static int pointRaceTimes = 1;

	private static List<List<Integer>> pointRaceList = new ArrayList<>();
	private static boolean isSignUp = true; // 是否可以开始报名

	@Override
	public void init() {
		super.init();
		fightService.addObserver(this);
	}

	@Override
	public void initService() {
		Scheduler scheduler = quartzManager.getScheduler();
		try {
			// 比赛入场
			JobDetail roleEntranceJob = JobBuilder.newJob(RoleEntranceJob.class)
					.withIdentity("roleEntranceJob", "jgroup").build();
			Trigger roleZeroTrigger = TriggerBuilder.newTrigger().withIdentity("roleEntranceTrigger", "triggerGroup")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 17 17 * * ?")).startNow().build();
			scheduler.scheduleJob(roleEntranceJob, roleZeroTrigger);

			// 比赛开始
			JobDetail startRaceJob = JobBuilder.newJob(StartRaceJob.class).withIdentity("startRaceJob", "jgroup")
					.build();
			Trigger startRaceZeroTrigger = TriggerBuilder.newTrigger().withIdentity("startRaceTrigger", "triggerGroup")
					.withSchedule(CronScheduleBuilder.cronSchedule("0 56 15 * * ?")).startNow().build();
			scheduler.scheduleJob(startRaceJob, startRaceZeroTrigger);

			Race race = new Race();
			race.setRaceId(1);
			race.setMaxSignUp(32);
			race.setWheelCount(4);
			int temp = 0;
			int wheelRound = 1;
			while (temp != race.getWheelCount()) {
				temp = race.getMaxSignUp() / 2;
				wheelRound += 1;
			}
			race.setWheelRound(wheelRound);
			RaceCache.getRaceMap().put(race.getRaceId(), race);

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public GeneratedMessage showRace(Role role, int gameType) {
		return null;
	}

	@Override
	public GeneratedMessage getRaceAward(Role role) {
		return null;
	}

	@Override
	public GeneratedMessage getRaceRoleNum(Role role) {
		// RaceCache.getRoleIdSet().size();
		int raceId = 1;
		RaceCache.getRaceMap().get(raceId);
		int state = 0;
		// 判定是否领奖
		if (RaceCache.getAward().get(role.getRoleId()) != null) {
			state = 4;// 需领奖
		} else if (RaceCache.getRoleIdSet().contains(role.getRoleId())) {
			state = 2; // 已报名
		} else if (RaceCache.getRoleIdSet().size() == RaceCache.getRaceSize()) {
			state = 3; // 已满
		} else {
			state = 1; // 可报名
		}

		return SCMessage
				.newBuilder()
				.setGetRaceRoleNumResponse(
						GetRaceRoleNumResponse.newBuilder().setRoleNum(RaceCache.getRoleIdSet().size()).setState(state))
				.build();
	}

	@Override
	public GeneratedMessage signUp(Role role) {
		// 检查是否开启
		int raceType = 1;
		Race race = RaceCache.getRaceMap().get(raceType);

		if (race.getRaceState() != RaceState.RACE_STATE_OPEN) {
			return SCMessage
					.newBuilder()
					.setRaceSignUpResponse(
							RaceSignUpResponse.newBuilder().setErrorCode(ErrorCode.RACE_NOT_SIGN_UP.getNumber()))
					.build();
		}
		synchronized (race) {
			if (race.getRaceState() != RaceState.RACE_STATE_OPEN) {
				// 不能报名
				return SCMessage
						.newBuilder()
						.setRaceSignUpResponse(
								RaceSignUpResponse.newBuilder().setErrorCode(ErrorCode.RACE_NOT_SIGN_UP.getNumber()))
						.build();
			}
			Set<Integer> roleIdSet = race.getRoleIdSet();
			if (!roleIdSet.contains(role.getRoleId())) {
				// ru guo bao guo yi ci ming
				return SCMessage
						.newBuilder()
						.setRaceSignUpResponse(
								RaceSignUpResponse.newBuilder().setErrorCode(ErrorCode.RACE_BEEN_SIGN_UP.getNumber()))
						.build();
			}
			roleIdSet.add(role.getRoleId());

			// 如果名额满了，关闭报名
			if (roleIdSet.size() >= race.getMaxSignUp()) {
				race.setRaceState(RaceState.RACE_STATE_OPEN_FILL);
			}

		}
		return SCMessage.newBuilder().setRaceSignUpResponse(RaceSignUpResponse.newBuilder()).build();
	}

	@Override
	public GeneratedMessage cancelSignUp(Role role) {
		// 检查是否开启
		int raceId = 1;
		Race race = RaceCache.getRaceMap().get(raceId);

		// 没有开始或已经关闭报名
		if (race.getRaceState() == RaceState.RACE_STATE_CLOSE || race.getRaceState() == RaceState.RACE_STATE_START) {
			// 如果游戏没有开始
			return SCMessage
					.newBuilder()
					.setRaceCancelSignUpResponse(
							RaceCancelSignUpResponse.newBuilder().setErrorCode(ErrorCode.RACE_NOT_SIGN_UP.getNumber()))
					.build();
		}

		Set<Integer> raceIdSet = race.getRoleIdSet();

		// 如果没有玩家
		if (!raceIdSet.contains(role.getRoleId())) {
			return SCMessage.newBuilder().setRaceCancelSignUpResponse(RaceCancelSignUpResponse.newBuilder()).build();
		}

		// 如果有玩家,则移除
		synchronized (race) {
			if (race.getRaceState() == RaceState.RACE_STATE_CLOSE || race.getRaceState() == RaceState.RACE_STATE_START) {
				return SCMessage
						.newBuilder()
						.setRaceCancelSignUpResponse(
								RaceCancelSignUpResponse.newBuilder().setErrorCode(
										ErrorCode.RACE_NOT_SIGN_UP.getNumber())).build();
			}
			raceIdSet.remove(role.getRoleId());
			if (race.getRaceState() == RaceState.RACE_STATE_OPEN_FILL) {
				race.setRaceState(RaceState.RACE_STATE_OPEN);
			}
		}

		return SCMessage.newBuilder().setRaceCancelSignUpResponse(RaceCancelSignUpResponse.newBuilder()).build();
	}

	@Override
	public void startRace(int raceId) {
		Race race = RaceCache.getRaceMap().get(raceId);
		race.setRaceState(RaceState.RACE_STATE_START);

		boolean initWheel = false;
		if (race.getRaceType() == RaceType.RACE_TYPE_ELIMINATE) {
			race.setCurrentRound(race.getCurrentRound() + 1);

			// 判断是否是车轮战
			race.setRaceType(race.getCurrentRound() >= race.getWheelRound() ? RaceType.RACE_TYPE_WHEEL : RaceType.RACE_TYPE_ELIMINATE);
			initWheel = race.getRaceType() == RaceType.RACE_TYPE_WHEEL;
		}

		// 如果是淘汰赛
		if (race.getRaceType() == RaceType.RACE_TYPE_ELIMINATE) {
			eliminateRaceStart(raceId);
		}

		// 如果是车轮战
		if (race.getRaceType() == RaceType.RACE_TYPE_WHEEL) {
			wheelRaceStart(raceId, initWheel);
		}

	}

	/**
	 * 淘汰赛开始
	 * 
	 * @param race
	 * @author wcy 2017年5月23日
	 */
	private void eliminateRaceStart(int raceId) {
		Race race = RaceCache.getRaceMap().get(raceId);
		// 如果是第一轮则加入到比赛队列
		if (race.getCurrentRound() == 1) {
			Map<Integer, Boolean> winRoleIdList = getRoundRoleIdList(race, race.getCurrentRound());
			for (Integer roleId : race.getRoleIdSet()) {
				winRoleIdList.put(roleId, false);
			}
		}

		List<Integer> roleIdList = new ArrayList<>(race.getRoundRoleIdMap().get(race.getCurrentRound()).keySet());
		// 多少对
		int pairSize = race.getMaxSignUp() / (race.getCurrentRound() * 2);
		for (int i = 0; i < roleIdList.size(); i++) {
			// 找后一个人的索引
			int findLastIndex = pairSize - i;
			if (roleIdList.size() < findLastIndex) {
				FightEventListener listener = new FightEventListenerAdapter(loginService.getRoleById(roleIdList.get(i))) {
					@Override
					public int getAI() {
						return this.getAIMapIdByPoint();
					}

					@Override
					public GameFightType getReturnType(Role role) {
						return GameFightType.TEST;
					}
				};
				matchService.matchRole(SessionCache.getSessionById(roleIdList.get(i)),
						loginService.getRoleById(roleIdList.get(i)), true, listener);
			} else {
				Role role1 = loginService.getRoleById(roleIdList.get(i));
				Role role2 = loginService.getRoleById(roleIdList.get(roleIdList.size() - 1));
				FightEventListener listener1 = new FightEventListenerAdapter(role1) {

				};
				FightEventListener listener2 = new FightEventListenerAdapter(role2) {

				};
				matchService.matchRole(role1, listener1, role2, listener2);
			}

		}
		int endTime = TimeUtils.getNowTime() + 5 * 60;
		race.setNextRoundStartTime(0);

		RaceRoundTimeUp raceTimeUp = this.createEliminateRaceStartEvent(race, endTime);

		// 发射时间截止的计时器
		eventScheduler.addEvent(raceTimeUp);
	}

	/**
	 * 车轮战开始
	 * 
	 * @param race
	 * @param initWheel
	 * @author wcy 2017年5月23日
	 */
	private void wheelRaceStart(int raceId, boolean initWheel) {
		Race race = RaceCache.getRaceMap().get(raceId);
		// 初始化车轮战需要的所有比赛
		if (initWheel) {
			if (!initWheel(race)) {
				// 比赛结束
				raceEnd(raceId);
				return;
			}
		}

		// 分开进行比赛
		List<List<Integer>> wheelRoleIdIndexList = race.getWheelRoleIdIndexList();
		List<List<Integer>> wheelCurrentRoleIdList = race.getWheelCurrentRoleIdList();
		// 清除还未比完的分组
		wheelCurrentRoleIdList.clear();
		// 获得下一轮分组赛
		List<List<Integer>> readyRaceRoleIdIndex = this.distributeRace(wheelRoleIdIndexList);
		if (readyRaceRoleIdIndex.size() == 0) {
			// 比赛结束

			return;
		}
		// 车轮战标记，用于放置定时器重复开始车轮战
		race.setWheelCurrentCount(race.getWheelCurrentCount() + 1);

		for (List<Integer> indexList : readyRaceRoleIdIndex) {
			int index1 = indexList.get(0);
			int index2 = indexList.get(1);

			int roleId1 = race.getWheelRoleIdList().get(index1);
			int roleId2 = race.getWheelRoleIdList().get(index2);

			if (roleId1 == 0 && roleId2 == 0) {
				// 如果是机器人就不用比了
				continue;
			}
			List<Integer> roleIdPointList = new ArrayList<>();
			roleIdPointList.add(roleId1);
			roleIdPointList.add(roleId2);

			wheelCurrentRoleIdList.add(roleIdPointList);
			// 有一方不是机器人就要比
			// TODO 比赛流程

		}
		int endTime = TimeUtils.getNowTime() + 5 * 60;

		// 发射时间截止的计时器
		eventScheduler.addEvent(this.createWheelRaceStartEvent(race, endTime));

	}

	/**
	 * 比赛结束
	 * 
	 * @param raceId
	 * @author wcy 2017年5月23日
	 */
	private void raceEnd(int raceId) {
		Race race = RaceCache.getRaceMap().get(raceId);
		race.setCurrentRound(0);
		race.setNextRoundStartTime(0);
		race.setNextWheelStartTime(0);
		race.setRaceState(RaceState.RACE_STATE_CLOSE);
		race.setRaceType(RaceType.RACE_TYPE_NONE);
		race.setWheelCurrentCount(0);

		// TODO 比赛结算

	}

	private List<List<Integer>> distributeRace(List<List<Integer>> list) {
		Set<Integer> set = new HashSet<>();
		List<List<Integer>> result = new ArrayList<>();
		List<List<Integer>> needDeleteList = new ArrayList<>(list.size());
		for (int index = 0; index < list.size(); index++) {
			List<Integer> s = list.get(index);
			HAS_RACE: {
				for (Integer index1 : s) {
					if (set.contains(index1)) {
						break HAS_RACE;
					}
				}

				// 如果索引指向的两个roleId都没有在比赛则就排在一起比赛
				set.addAll(s);
				result.add(s);
				needDeleteList.add(s);
			}
		}

		// 命中后移除组合
		for (List<Integer> needRemoveList : needDeleteList) {
			list.remove(needRemoveList);
		}
		return result;
	}

	private boolean initWheel(Race race) {
		Map<Integer, Boolean> map = this.getRoundRoleIdList(race, race.getCurrentRound());
		if (map.size() == 0) {
			// 淘汰赛真人全部淘汰，则不用进行车轮赛了
			return false;
		}
		List<Integer> wheelList = race.getWheelRoleIdList();
		wheelList.addAll(map.keySet());
		int addCount = race.getWheelCount() - wheelList.size();
		// 如果车轮赛人数不到则加入ai，roleId是0
		for (int i = 0; i < addCount; i++) {
			wheelList.add(0);
		}
		int size = wheelList.size();
		List<List<Integer>> list = wheelBuildGroup(size);
		race.getWheelRoleIdIndexList().addAll(list);
		return true;
	}

	private List<List<Integer>> wheelBuildGroup(int size) {
		// 根据索引进行排列组合
		int total = size * (size - 1) / 2;// 排列组合的总和
		List<List<Integer>> list = new ArrayList<>(total);
		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				List<Integer> roleIdIndexList = new ArrayList<>(2);
				roleIdIndexList.add(i);
				roleIdIndexList.add(j);
				list.add(roleIdIndexList);
			}
		}
		return list;
	}

	private RaceRoundTimeUp createEliminateRaceStartEvent(Race race, int endTime) {
		RaceRoundTimeUp raceTimeUp = new RaceRoundTimeUp() {

			@Override
			public void nextRound(int raceId) {
				startRace(raceId);
			}
		};
		raceTimeUp.setEndTime(endTime);
		raceTimeUp.setRaceId(race.getRaceId());
		raceTimeUp.setTargetRound(race.getCurrentRound());

		return raceTimeUp;
	}

	private RaceWheelTimeUp createWheelRaceStartEvent(Race race, int endTime) {
		RaceWheelTimeUp raceTimeUp = new RaceWheelTimeUp() {

			@Override
			public void nextRound(int raceId) {
				startRace(raceId);
			}
		};
		raceTimeUp.setEndTime(endTime);
		raceTimeUp.setRaceId(race.getRaceId());
		raceTimeUp.setTargetRound(race.getWheelCurrentCount());

		return raceTimeUp;
	}

	private Map<Integer, Boolean> getRoundRoleIdList(Race race, int round) {
		Map<Integer, Map<Integer, Boolean>> roundRoleIdMap = race.getRoundRoleIdMap();
		Map<Integer, Boolean> roundRoleIdList = roundRoleIdMap.get(round);
		if (roundRoleIdList != null) {
			return roundRoleIdList;
		}
		synchronized (roundRoleIdMap) {
			roundRoleIdList = roundRoleIdMap.get(round);
			if (roundRoleIdList != null) {
				return roundRoleIdList;
			}
			roundRoleIdList = new HashMap<>();
			race.getRoundRoleIdMap().put(race.getCurrentRound(), roundRoleIdList);
		}

		return roundRoleIdList;
	}

	@Override
	public void update(Observer observer, String msg, Object... args) {

		if (msg.equals(FightConstant.NOTICE_GAME_RESULT)) {
			@SuppressWarnings("unchecked")
			List<Integer> roleIdList = (List<Integer>) args[0];
			int sendRoleId = (int) args[1];
			GameResult gameResult = (GameResult) args[2];

			this.gameOver(roleIdList, sendRoleId, gameResult);

		}

		// if (!msg.equals(FightConstant.NOTICE_GAME_RESULT)) {
		// return;
		// }
		// @SuppressWarnings("unchecked")
		// List<Integer> roleList = (List<Integer>) args[0];
		// Integer winId = (Integer) args[1];
		//
		// for (int i = 0; i < roleList.size(); i++) {
		// if (roleList.get(i) != 0) {
		// sum++;
		// }
		// }
		// if (winId != 0) {
		// List<Integer> pointRaceList = RaceCache.getRoleRace().get(target);
		// int index = pointRaceList.indexOf(winId);
		// RaceCache.getRoleRace().get(target).remove(index);
		// if (RaceCache.getRoleRace().get(target / 2) == null) {
		// List<Integer> list = new ArrayList<>();
		// RaceCache.getRoleRace().put(target / 2, list);
		// }
		// RaceCache.getRoleRace().get(target / 2).add(winId);
		// }
		// target2 = target;
		// System.out.println(sum);
		// // System.out.println(winId);
		// List list = RaceCache.getRoleRace().get(target);
		// List list2 = RaceCache.getRoleRace().get(target / 2);
		// System.out.println(list);
		// System.out.println(list2);
		// //
		// System.out.println(RaceCache.getRoleRace().get(target).indexOf(winId));
		//
		// // ruguo dou canjia wanle ,jinruxiayilun
		// if (sum == RaceCache.getRoleRace().get(target).size() +
		// RaceCache.getRoleRace().get(target / 2).size()) {
		// sum = 0;
		// if (target != 4) {
		// target = target / 2;
		// startRace(target);
		// } else {
		// if (pointRaceTimes > target - 1) {
		//
		// isSignUp = true; // 积分赛结束 ，可以报名
		// pointRaceTimes = 1;
		// } else {
		// pointRace(target, pointRaceTimes);
		// pointRaceTimes++;
		// }
		// }
		// }
	}

	private void gameOver(List<Integer> roleIdList, Integer sendRoleId, GameResult gameResult) {
		// 获得赢家
		int winRoleId = getWinRoleId(roleIdList, sendRoleId, gameResult);
		Map<Integer, Race> raceMap = RaceCache.getRaceMap();
		for (Race race : raceMap.values()) {
			RaceState raceState = race.getRaceState();
			int round = race.getCurrentRound();
			// 如果比赛开始，并且当局有这个玩家
			if (raceState == RaceState.RACE_STATE_START) {
				// 淘汰赛
				if (race.getRaceType() == RaceType.RACE_TYPE_ELIMINATE) {
					// 如果当前局有该玩家,则加入到下一局中
					Map<Integer, Boolean> roundRoleIdList = getRoundRoleIdList(race, round);
					if (roundRoleIdList.containsKey(sendRoleId)) {
						// 标记两个人比过赛了,因为可能是npc所以要检查,只给真正的玩家赋值为true
						for (int roleId : roleIdList) {
							if (roundRoleIdList.containsKey(roleId)) {
								roundRoleIdList.put(sendRoleId, true);
							}
						}

						// 获得下一轮映射表,并附上没有比赛的标记
						Map<Integer, Boolean> nextRoundRoleIdList = getRoundRoleIdList(race, round + 1);
						nextRoundRoleIdList.put(winRoleId, false);
						// 如果是该场的最后一个人则开始下一轮
						boolean currentRoundEnd = true;
						for (boolean hasBattle : nextRoundRoleIdList.values()) {
							if (!hasBattle) {
								currentRoundEnd = false;
								break;
							}
						}
						// 如果是该场的最后一个人则开始下一轮
						if (currentRoundEnd) {
							RaceRoundTimeUp raceRoundTimeUp = createEliminateRaceStartEvent(race,
									TimeUtils.getNowTime() + 60);
							race.setNextRoundStartTime(raceRoundTimeUp.getEndTime());

							eventScheduler.addEvent(raceRoundTimeUp);
						}

					}
				}

				// 车轮战
				else if (race.getRaceType() == RaceType.RACE_TYPE_WHEEL) {
					// 移除该组
					List<List<Integer>> list = race.getWheelCurrentRoleIdList();
					List<Integer> deleteList = null;
					for (int i = 0; i < list.size(); i++) {
						List<Integer> pointRoleIdList = list.get(i);
						for (int joinPointRoleId : pointRoleIdList) {
							if (sendRoleId == joinPointRoleId) {
								deleteList = pointRoleIdList;
								break;
							}
						}
					}
					if (deleteList != null) {
						list.remove(deleteList);
					}

					// 检查是否全部结束
					boolean currentPointEnd = list.size() == 0;
					if (currentPointEnd) {
						if (race.getWheelCurrentCount() >= race.getWheelCount() - 1) {
							// 比赛结束
							raceEnd(race.getRaceId());
							continue;
						}
						RaceWheelTimeUp raceRoundTimeUp = createWheelRaceStartEvent(race, TimeUtils.getNowTime() + 60);
						// 下一轮车轮战开始时间
						race.setNextRoundStartTime(raceRoundTimeUp.getEndTime());
						// 发射下一轮车轮战消息
						eventScheduler.addEvent(raceRoundTimeUp);
					}
				}

			}
		}

	}

	private int getWinRoleId(List<Integer> roleIdList, Integer sendRoleId, GameResult gameResult) {
		// 把胜者拿出来放到下一局
		int winRoleId = 0;
		if (gameResult == GameResult.LOSS) {
			for (int roleId : roleIdList) {
				if (roleId != sendRoleId) {
					winRoleId = roleId;
					break;
				}
			}
		}
		return winRoleId;
	}

	@Override
	public GeneratedMessage showNextRace(Role role) {
		// if (!RaceCache.getRoleIdSet().contains(role.getRoleId())) {
		// return SCMessage.newBuilder()
		// .setRaceDescResponse(RaceDescResponse.newBuilder().setRaceType(0).setRaceTime(0)).build();
		// }
		// int time = 0;
		// int type = 0;
		// if (target != 4) {
		// if (RaceCache.getRoleRace().get(target2 /
		// 2).contains(role.getRoleId())) {
		// time = RaceCache.getRaceSize() / target2;
		// type = 2;
		// } else {
		// type = -1;
		// }
		// } else {
		// time = RaceServiceImpl.getPointRaceTimes() - 1;
		// type = 3;
		// }

		int nowTime = TimeUtils.getNowTime();
		Map<Integer, Race> raceMap = RaceCache.getRaceMap();
		for (Race race : raceMap.values()) {
			if (race.getRaceState() != RaceState.RACE_STATE_START)
				continue;
			if (race.getRoleIdSet().contains(role.getRoleId())) {

				// 获得下一轮数据
				Map<Integer, Boolean> map = this.getRoundRoleIdList(race, race.getCurrentRound() + 1);
				RaceType raceType = RaceType.RACE_TYPE_NONE;
				if (map.containsKey(role.getRoleId())) {
					raceType = race.getCurrentRound() + 1 >= race.getWheelRound() ? RaceType.RACE_TYPE_WHEEL : RaceType.RACE_TYPE_ELIMINATE;
				}
				int deltaTime = 0;
				if (raceType == RaceType.RACE_TYPE_ELIMINATE) {
					deltaTime = race.getNextRoundStartTime() - nowTime;
				} else if (raceType == RaceType.RACE_TYPE_WHEEL) {
					deltaTime = race.getNextWheelStartTime() - nowTime;
				}
				if (deltaTime < 0)
					deltaTime = 0;
				return SCMessage
						.newBuilder()
						.setRaceDescResponse(RaceDescResponse.newBuilder().setRaceTime(deltaTime).setRaceType(raceType))
						.build();
			}
		}
		return SCMessage.newBuilder()
				.setRaceDescResponse(RaceDescResponse.newBuilder().setRaceType(RaceType.RACE_TYPE_NONE).setRaceTime(0))
				.build();

	}

	@Override
	public void pointRace(Integer size, Integer times) {
		if (times == 1) {
			pointRaceList();
		}
		// 积分匹配
		List<Integer> list = new ArrayList<>();
		if (RaceCache.getRoleRace().get(target).isEmpty() || RaceCache.getRoleRace().get(target) == null) {
			return;
		}

		int index = pointRaceList.get(0).get(pointRaceTimes - 1); // 获得匹配对手

		if (RaceCache.getRoleRace().get(target).get(index) == 0) {
			FightEventListener listener = new FightEventListenerAdapter(loginService.getRoleById(RaceCache
					.getRoleRace().get(target).get(0))) {
				@Override
				public int getAI() {
					return this.getAIMapIdByPoint();
				}

				@Override
				public GameFightType getReturnType(Role role) {
					return GameFightType.TEST;
				}
			};
			matchService.matchRole(SessionCache.getSessionById(RaceCache.getRoleRace().get(target).get(0)),
					loginService.getRoleById(RaceCache.getRoleRace().get(target).get(0)), true, listener);
		} else {
			Role role1 = loginService.getRoleById(RaceCache.getRoleRace().get(target).get(0));
			Role role2 = loginService.getRoleById(RaceCache.getRoleRace().get(target).get(index));
			FightEventListener listener1 = new FightEventListenerAdapter(role1) {

			};
			FightEventListener listener2 = new FightEventListenerAdapter(role2) {

			};
			matchService.matchRole(role1, listener1, role2, listener2);
		}

		list.add(index);

		for (int i = 1; i < pointRaceList.size(); i++) {

			if (RaceCache.getRoleRace().get(target).get(i) == 0) {
				// npc 打 Npc

			}
			if (list.contains(i))
				continue;
			for (int j = 0; j < pointRaceList.get(i).size(); j++) {
				if (list.contains(pointRaceList.get(i).get(j)))
					continue;
				else {

					if (RaceCache.getRoleRace().get(target).get(j) == 0) {
						FightEventListener listener = new FightEventListenerAdapter(loginService.getRoleById(RaceCache
								.getRoleRace().get(target).get(i))) {
							@Override
							public int getAI() {
								return this.getAIMapIdByPoint();
							}

							@Override
							public GameFightType getReturnType(Role role) {
								return GameFightType.TEST;
							}
						};
						matchService.matchRole(SessionCache.getSessionById(RaceCache.getRoleRace().get(target).get(i)),
								loginService.getRoleById(RaceCache.getRoleRace().get(target).get(i)), true, listener);
					} else {
						Role role1 = loginService.getRoleById(RaceCache.getRoleRace().get(target).get(i));
						Role role2 = loginService.getRoleById(RaceCache.getRoleRace().get(target).get(j));
						FightEventListener listener1 = new FightEventListenerAdapter(role1) {

						};
						FightEventListener listener2 = new FightEventListenerAdapter(role2) {

						};
						matchService.matchRole(role1, listener1, role2, listener2);
					}
					break;
				}
			}
		}
	}

	public void pr(int raceId) {
		Race race = RaceCache.getRaceMap().get(raceId);
		race.getRoundRoleIdMap();

	}

	public void pointRaceList() {
		for (int i = RaceCache.getRoleRace().get(target).size(); i < target; i++) {
			RaceCache.getRoleRace().get(target).add(0);
		}
		for (int i = 0; i < target; i++) {
			List<Integer> list = new ArrayList<>();

			for (int j = i + 1; j < target; j++) {
				list.add(j);
			}
			pointRaceList.add(list);
		}
	}

	@Override
	public void sendRaceAward() {
		int size = RaceCache.getRaceSize();
		for (int t = 0; size != 4; size = size / 2, t++) {
			for (int i = 0; i < RaceCache.getRoleRace().get(size).size(); i++) {
				RaceCache.getAward().put(RaceCache.getRoleRace().get(size).get(i), t);

			}
		}
		// 发前四名的奖
		{
			RaceCache.getAward().put(0, 100);
		}

		RaceCache.getResult().clear();
		RaceCache.getRoleIdSet().clear();
		RaceCache.getRoleRace().clear();

		isSignUp = true;
	}

	public static boolean isSignUp() {
		return isSignUp;
	}

	public static int getTarget() {
		return target;
	}

	public static int getPointRaceTimes() {
		return pointRaceTimes;
	}

	public static void setSignUp(boolean isSignUp) {
		RaceServiceImpl.isSignUp = isSignUp;
	}

	public static void main(String[] args) {
		RaceServiceImpl impl = new RaceServiceImpl();
		int size = 6;
		List list = impl.wheelBuildGroup(size);
		System.out.println(list);
		for (int i = 0; i < size - 1; i++) {
			System.out.println(impl.distributeRace(list));
		}

	}
}
