package com.randioo.shizurongyao_server.module.gm.service;

import java.util.Collection;
import java.util.Iterator;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.navigation.Navigation;
import com.randioo.randioo_server_base.net.IActionSupport;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.scheduler.SchedulerManager;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.system.GlobleConfig;
import com.randioo.randioo_server_base.utils.system.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.utils.system.Platform;
import com.randioo.randioo_server_base.utils.system.Platform.OS;
import com.randioo.randioo_server_base.utils.system.SignalTrigger;
import com.randioo.randioo_server_base.utils.template.Function;
import com.randioo.shizurongyao_server.SessionCloseHandler;
import com.randioo.shizurongyao_server.dao.StoreVideoDao;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.bo.StoreVideo;
import com.randioo.shizurongyao_server.entity.po.Video;
import com.randioo.shizurongyao_server.entity.po.VideoManager;
import com.randioo.shizurongyao_server.formatter.StoreVideoFormatter;
import com.randioo.shizurongyao_server.module.fight.service.FightService;
import com.randioo.shizurongyao_server.module.login.service.LoginService;
import com.randioo.shizurongyao_server.module.race.service.RaceService;
import com.randioo.shizurongyao_server.module.rank.service.RankService;
import com.randioo.shizurongyao_server.module.role.service.RoleService;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Gm.GmCmdResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.util.SessionUtils;

@Service("gmService")
public class GmServiceImpl extends ObserveBaseService implements GmService {

	@Autowired
	private StoreVideoDao storeVideoDao;

	@Autowired
	private SchedulerManager schedulerManager;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private LoginService loginService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private RankService rankService;

	@Autowired
	private RaceService raceService ;
	@Autowired
	private FightService fightService;

	@Override
	public void init() {

		Function function = new Function() {

			@Override
			public Object apply(Object... params) {
				GlobleConfig.set(GlobleEnum.LOGIN, false);

				System.out.println("port close");

				everybodyOffline();

				// 定时器全部停止
				try {
					schedulerManager.shutdown(1, TimeUnit.SECONDS);
				} catch (Exception e1) {
					e1.printStackTrace();
				}

				try {
					gameDB.shutdown(1, TimeUnit.SECONDS, new Function() {

						@Override
						public Object apply(Object... params) {
							// 数据保存
							System.out.println();
							System.out.println("start game all data save");
							// 循环性数据存储
							ExecutorService service = loopSaveData(true, true, 2);

							service.shutdown();
							try {
								while (!service.awaitTermination(1, TimeUnit.SECONDS)) {
								}
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
							// 一次性数据存储
							onceSaveData();

							System.out.println();
							System.out.println("game all data save SUCCESS");
							System.exit(0);
							return null;
						}
					});
				} catch (Exception e) {
					e.printStackTrace();
				}

				return null;
			}

		};

		// 命令关闭信号
		try {
			System.out.println(Platform.getOS());
			if (Platform.getOS() == OS.WIN)
				SignalTrigger.setSignCallback("INT", function);
			else if (Platform.getOS() == OS.LINUX)
				SignalTrigger.setSignCallback("ABRT", function);
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (Platform.getOS() == OS.WIN) {
			new Thread(new DBRunnable<Function>(function) {

				private Scanner in = new Scanner(System.in);

				@Override
				public void run(Function function) {
					while (true) {
						try {
							String command = in.nextLine();
							if (!StringUtils.isNullOrEmpty(command)) {
								if (command.equals("exit")) {
									function.apply();
								} else if (command.equals("save")) {
									for (RoleInterface roleInterface : RoleCache.getRoleMap().values()) {
										SessionCloseHandler.roleDataCache2DB((Role) roleInterface, true);
										System.out.println(roleInterface.getAccount() + " force Save");
									}
								}
							}
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

			}).start();

		}
	}

	/**
	 * 所有人下线
	 * 
	 * @author wcy 2016年12月9日
	 */
	private void everybodyOffline() {
		// 所有人下线
		Collection<IoSession> allSession = SessionCache.getAllSession();
		Iterator<IoSession> it = allSession.iterator();
		while (it.hasNext()) {
			it.next().close(true);
		}

	}

	@Override
	public void loopSaveData(boolean mustSave) {
		loopSaveData(mustSave, false, 0);
	}

	private ExecutorService loopSaveData(final boolean mustSave, boolean thread, int threadSize) {
		ExecutorService executorService = null;
		if (thread) {
			executorService = Executors.newScheduledThreadPool(threadSize);
		}
		for (RoleInterface roleInterface : RoleCache.getRoleMap().values()) {
			if (executorService != null) {
				executorService.submit(new DBRunnable<RoleInterface>(roleInterface) {

					@Override
					public void run(RoleInterface roleInterface) {
						saveRoleData(roleInterface, mustSave);
					}
				});
			} else {
				saveRoleData(roleInterface, mustSave);
			}
		}

		return executorService;

	}

	private void saveRoleData(RoleInterface roleInterface, boolean mustSave) {
		try {
			Role role = (Role) roleInterface;
			if (mustSave)
				System.out.println("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:"
						+ role.getName() + "] save success");
			SessionCloseHandler.roleDataCache2DB(role, mustSave);
		} catch (Exception e) {
			System.out.println("Role: " + roleInterface.getRoleId() + " saveError!");
			e.printStackTrace();
		}
	}

	private void onceSaveData() {
		saveVideo();
	}

	/**
	 * 保存记录
	 * 
	 * @author wcy 2016年12月9日
	 */
	private void saveVideo() {
		for (Video video : VideoManager.getAllVideo().values()) {
			try {
				StoreVideo storeVideo = StoreVideoFormatter.format(video);
				storeVideoDao.insert(storeVideo);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	@Override
	public void command(String command, IoSession session) {
		SessionUtils.sc(session, SCMessage.newBuilder().setGmCmdResponse(GmCmdResponse.newBuilder()).build());

		String[] cmds = command.split("\\|");
		String cmd = cmds[0];

		IActionSupport actionSupport = Navigation.getAction(cmd);
		try {
			if (actionSupport != null && GlobleConfig.Boolean(GlobleEnum.GM)) {
				String params = null;
				try {
					params = cmds[1];
				} catch (Exception e) {
				}
				actionSupport.execute(params, session);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void money(Role role, int money) {
		roleService.addMoney(role, money);
	}

	@Override
	public void god(Role role) {
		roleService.addMoney(role, 10000);
		roleService.addGold(role, 10000);
	}

	@Override
	public void rankRefresh() {
		rankService.refreshPointRank();
	}

	@Override
	public void startMatch() {
		// TODO Auto-generated method stub
		raceService.startRace(32);
	}

	@Override
	public void gameOver(Role role) {
		fightService.offline(role,GameResult.WIN);
	}
}
