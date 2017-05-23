package com.randioo.shizurongyao_server.module.pillage.service;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.local.CompetitionCache;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.FightEventListenerAdapter;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.module.card.service.CardService;
import com.randioo.shizurongyao_server.module.fight.FightConstant.GameFightType;
import com.randioo.shizurongyao_server.module.match.service.MatchService;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageCancelResponse;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageCompetitionNoticeResponse;
import com.randioo.shizurongyao_server.protocol.Pillage.PillagePracticeResponse;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageRoleResponse;
import com.randioo.shizurongyao_server.protocol.Pillage.PillageShowResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.util.HttpVisitor;

@Service("pillageService")
public class PillageServiceImpl extends ObserveBaseService implements PillageService {

	@Autowired
	private CardService cardService;

	@Autowired
	private MatchService matchService;

	@Autowired
	private HttpVisitor httpVisitor;

	public void setHttpVisitor(HttpVisitor httpVisitor) {
		this.httpVisitor = httpVisitor;
	}

	@Override
	public void init() {
		httpVisitor.init();
	}

	/**
	 * 点击 匹配按钮
	 */
	@Override
	public void choose(Role role, boolean isAI, IoSession session) {
		// if(role.getOwlofwarGame() !=null){
		// session.write(SCMessage.newBuilder()
		// .setPillageRoleResponse(PillageRoleResponse.newBuilder().setErrorCode(ErrorCode.IN_THE_ROOM)).build());
		// return;
		// }
		role.setOwlofwarGame(null);
		session.write(SCMessage.newBuilder()
				.setPillageRoleResponse(PillageRoleResponse.newBuilder()).build());

		FightEventListener listener = new FightEventListenerAdapter(role) {

			@Override
			public void matchFighter(OwlofwarGame game, Role role) {
				// role.setPlayTime(role.getPlayTime() - 1);

				// int moneyCount = getRoleMoney(getRole());
				// Resource res = new Resource();
				// res.setItemId(0);
				// res.setNum(moneyCount);
				// res.setResType(IncomeConstant.ITEM_TYPE_MONEY);
				// String award = GameUtils.formatResources(res);
				// setAward(award);
			}

			@Override
			public void afterEnd(OwlofwarGame game, byte result) {

				if (!game.isAIGame()) {
					// int nowTime = TimeUtils.getNowTime();
					//
					// Map<Integer, Integer> roleScoreMap = game.getScoreMap();
					// FightMailData data =
					// game.getRoleGameInfoMap().get(getRole().getRoleId()).getFightMailData();
					// int roleScore = roleScoreMap.get(data.getRoleId());
					// int enemyScore = roleScoreMap.get(data.getEnemyId());
					//
					// data.setEnemyScore(enemyScore);
					// data.setRoleScore(roleScore);
					// data.setFightId(video.getVideoId());

				}

				// Set<Resource> resources =
				// GameUtils.formatResouces(this.getAward());
				// for (Resource resource : resources) {
				// int num = resource.getNum();
				// if (result == FightConstant.WIN) {
				// resource.setNum(Math.abs(num));
				// incomeService.award(getRole(),
				// GameUtils.formatResources(resources));
				// } else if (result == FightConstant.LOSS) {
				// resource.setNum(-Math.abs(num));
				// incomeService.award(getRole(),
				// GameUtils.formatResources(resources));
				// }
				//
				// }
			}

			@Override
			public int getAI() {
				return this.getAIMapIdByPoint();
			}

			@Override
			public GameFightType getReturnType(Role role) {
				// TODO Auto-generated method stub
				return GameFightType.PILLAGE;
			}

		};

		// RoomCache.addRoom(role, isAI, listener);
		matchService.matchRole(session, role, isAI, listener);

	}

	@Override
	public void practice(Role role, IoSession session) {
		// TODO Auto-generated method stub
		role.setOwlofwarGame(null);
		session.write(SCMessage.newBuilder().setPillagePracticeResponse(PillagePracticeResponse.newBuilder()).build());

		FightEventListener listener = new FightEventListenerAdapter(role) {

			@Override
			public int getAI() {
				return this.getAIMapIdByPoint();
			}

			@Override
			public GameFightType getReturnType(Role role) {
				// TODO Auto-generated method stub
				return GameFightType.TEST;
			}

		};

		matchService.matchRole(session, role, true, listener);
	}

	@Override
	public void cancelChoose(Role role, IoSession session) {
		session.write(SCMessage.newBuilder()
				.setPillageCancelResponse(PillageCancelResponse.newBuilder()).build());
		matchService.cancelMatch(role);
	}

	@Override
	public GeneratedMessage showMatchingInfo(Role role) {
		return SCMessage.newBuilder()
				.setPillageShowResponse(PillageShowResponse.newBuilder().setPoint(role.getPoint())).build();
	}

	@Override
	public void competitionNotice(Role role, int competitionId, IoSession session) {
		session.write(SCMessage.newBuilder()
				.setPillageCompetitionNoticeResponse(PillageCompetitionNoticeResponse.newBuilder()).build());

		boolean complete = false;
		Set<Integer> roleIdSet = new HashSet<>();
		synchronized (role) {
			Set<Integer> set = CompetitionCache.getCompetitionSetById(competitionId);
			if (set == null) {
				set = new HashSet<>();
				CompetitionCache.getCompetitionMap().put(competitionId, set);
			}

			set.add(role.getRoleId());

			if (set.size() == 2) {
				complete = true;
				CompetitionCache.getCompetitionMap().remove(competitionId);
				roleIdSet = set;
			}
			if (!complete)
				return;
		}

		Map<String, Object> map = new HashMap<>();
		map.put("key", "f4f3f65d6d804d138043fbbd1843d510");
		map.put("id", competitionId + "");
		try {
			String info = httpVisitor.httpGetRequest(map);
			List<Role> roleList = new ArrayList<>(2);
			List<FightEventListener> listenerList = new ArrayList<>(2);
			for (Integer roleId : roleIdSet) {
				roleList.add((Role) RoleCache.getRoleById(roleId));
				listenerList.add(new FightEventListenerAdapter(role) {
					@Override
					public int getAI() {
						return this.getAIMapIdByPoint();
					}

					@Override
					public GameFightType getReturnType(Role role) {
						// TODO Auto-generated method stub
						return GameFightType.PILLAGE;
					}
				});
			}
			Role role1 = roleList.get(0);
			FightEventListener listener1 = listenerList.get(0);
			Role role2 = roleList.get(1);
			FightEventListener listener2 = listenerList.get(1);

			if (this.check(role1, role2, info)) {
				matchService.matchRole(role1, listener1, role2, listener2);
			}
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private boolean check(Role role1, Role role2, String info) {

		return true;
	}

}
