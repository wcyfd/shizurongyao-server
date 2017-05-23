package com.randioo.shizurongyao_server.module.match.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.module.match.MatchHandler;
import com.randioo.randioo_server_base.module.match.MatchInfo;
import com.randioo.randioo_server_base.module.match.MatchModelService;
import com.randioo.randioo_server_base.module.match.MatchRule;
import com.randioo.randioo_server_base.module.match.Matchable;
import com.randioo.randioo_server_base.utils.game.IdClassCreator;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.file.CardConfigCache;
import com.randioo.shizurongyao_server.cache.file.ExtraCardConfigCache;
import com.randioo.shizurongyao_server.cache.file.MapConfigCache;
import com.randioo.shizurongyao_server.cache.file.NPCPlayerConfigCache;
import com.randioo.shizurongyao_server.dao.StoreVideoDao;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.file.MapConfig;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGameInfo;
import com.randioo.shizurongyao_server.entity.po.OwlofwarMatchInfo;
import com.randioo.shizurongyao_server.entity.po.OwlofwarMatchRule;
import com.randioo.shizurongyao_server.formatter.NPCPlayerConfigFormatter;
import com.randioo.shizurongyao_server.protocol.Entity.CardNPCType;
import com.randioo.shizurongyao_server.protocol.Entity.MatchRoleInfo;
import com.randioo.shizurongyao_server.protocol.Match.SCMatchCancel;
import com.randioo.shizurongyao_server.protocol.Match.SCMatchComplete;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.util.SessionUtils;

@Service("matchService")
public class MatchServiceImpl extends ObserveBaseService implements MatchService {

	// private FightService fightService;
	//
	// public void setFightService(FightService fightService) {
	// this.fightService = fightService;
	// }

	// private GameMatcher gameMatcher;
	//
	// public void setGameMatcher(GameMatcher gameMatcher) {
	// this.gameMatcher = gameMatcher;
	// }

	@Autowired
	private MatchModelService matchModelService;

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private StoreVideoDao storeVideoDao;

	@Override
	public void init() {
		Integer maxGameId = storeVideoDao.getMaxStoreVideoId();
		idClassCreator.initId(OwlofwarGame.class, maxGameId == null ? 0 : maxGameId);

		init2();
	}

	void init1() {
		matchModelService.setMatchHandler(new MatchHandler() {

			@Override
			public MatchInfo createMatchInfo(MatchRule matchRule) {
				System.out.println("createMatchInfo");
				OwlofwarMatchRule owlofwarMatchRule = (OwlofwarMatchRule) matchRule;
				Role role = (Role) matchRule.getMatchTarget();

				OwlofwarMatchInfo matchInfo = new OwlofwarMatchInfo();
				matchInfo.getFightEventListeners().put(role.getRoleId(), owlofwarMatchRule.getFightEventListener());
				System.out.println(matchInfo);
				return matchInfo;
			}

			@Override
			public void matchSuccess(MatchInfo matchInfo, MatchRule matchRule) {
				System.out.println("matchSuccess");
				OwlofwarMatchInfo owlofwarMatchInfo = (OwlofwarMatchInfo) matchInfo;
				OwlofwarMatchRule owlofwarMatchRule = (OwlofwarMatchRule) matchRule;
				Role role = (Role) owlofwarMatchRule.getMatchTarget();
				FightEventListener listener = owlofwarMatchRule.getFightEventListener();
				owlofwarMatchInfo.getFightEventListeners().put(role.getRoleId(), listener);
				System.out.println(owlofwarMatchInfo);
			}

			@Override
			public void matchComplete(MatchInfo matchInfo) {
				System.out.println("matchComplete");
				OwlofwarGame game = serverGameInit((OwlofwarMatchInfo) matchInfo);
				startGame(game);
			}

			@Override
			public boolean needWaitMatch(MatchInfo matchInfo) {
				System.out.println("needWaitMatch");
				return true;
			}

			@Override
			public boolean matchRule(MatchRule myMatchRule, MatchInfo otherMatchInfo) {
				System.out.println("matchRule");
				return otherMatchInfo.getMatchRule().getPlayerCount() == myMatchRule.getPlayerCount();
			}

			@Override
			public MatchRule getAutoMatchRole(MatchInfo matchInfo) {
				System.out.println("getAutoMatchRole");
				OwlofwarMatchInfo owlofwarMatchInfo = (OwlofwarMatchInfo) matchInfo;
				Map<Integer, FightEventListener> listeners = owlofwarMatchInfo.getFightEventListeners();
				MatchRule matchRule = null;
				for (FightEventListener listener : listeners.values()) {
					matchRule = getAI(owlofwarMatchInfo, listener);
					break;
				}
				return matchRule;
			}

			@Override
			public void destroyMatchInfo(MatchInfo matchInfo) {
				System.out.println("destroyMatchInfo");

			}

			@Override
			public void cancelMatch(Matchable matchable) {
				System.out.println("cancel match success");
				Role role = (Role) matchable;
				IoSession session = SessionCache.getSessionById(role.getRoleId());
				session.write(SCMessage.newBuilder().setSCMatchCancel(SCMatchCancel.newBuilder()).build());
			}

			@Override
			public void changeStartMatcher(Matchable originStarter, Matchable newStarter) {
				// TODO Auto-generated method stub

			}

			@Override
			public void waitClick(MatchInfo matchInfo, int clickCount) {
				Role role = (Role) matchInfo.getMatchRule().getMatchTarget();
				System.out.println("owlofwar match clickCount:" + clickCount + " start account:" + role.getAccount()
						+ " start name:" + role.getName() + " wait id:" + matchInfo.getMatchId());
			}
		});
	}

	void init2() {
		matchModelService.init();
		matchModelService.setMatchHandler(new MatchHandler() {

			@Override
			public MatchInfo createMatchInfo(MatchRule matchRule) {
				System.out.println("createMatchInfo");
				OwlofwarMatchRule owlofwarMatchRule = (OwlofwarMatchRule) matchRule;
				Role role = (Role) matchRule.getMatchTarget();

				OwlofwarMatchInfo matchInfo = new OwlofwarMatchInfo();
				matchInfo.getFightEventListeners().put(role.getRoleId(), owlofwarMatchRule.getFightEventListener());
				System.out.println(matchInfo);
				return matchInfo;
			}

			@Override
			public void matchSuccess(MatchInfo matchInfo, MatchRule matchRule) {
				System.out.println("matchSuccess");
				OwlofwarMatchInfo owlofwarMatchInfo = (OwlofwarMatchInfo) matchInfo;
				OwlofwarMatchRule owlofwarMatchRule = (OwlofwarMatchRule) matchRule;
				Role role = (Role) owlofwarMatchRule.getMatchTarget();
				FightEventListener listener = owlofwarMatchRule.getFightEventListener();
				owlofwarMatchInfo.getFightEventListeners().put(role.getRoleId(), listener);
				System.out.println(owlofwarMatchInfo);
			}

			@Override
			public void matchComplete(MatchInfo matchInfo) {
				System.out.println("matchComplete");
				OwlofwarGame game = serverGameInit((OwlofwarMatchInfo) matchInfo);
				startGame(game);
			}

			@Override
			public boolean needWaitMatch(MatchInfo matchInfo) {
				System.out.println("needWaitMatch");
				return true;
			}

			@Override
			public boolean matchRule(MatchRule myMatchRule, MatchInfo otherMatchInfo) {
				System.out.println("matchRule");
				return otherMatchInfo.getMatchRule().getPlayerCount() == myMatchRule.getPlayerCount();
			}

			@Override
			public MatchRule getAutoMatchRole(MatchInfo matchInfo) {
				System.out.println("getAutoMatchRole");
				OwlofwarMatchInfo owlofwarMatchInfo = (OwlofwarMatchInfo) matchInfo;
				Map<Integer, FightEventListener> listeners = owlofwarMatchInfo.getFightEventListeners();
				MatchRule matchRule = null;
				for (FightEventListener listener : listeners.values()) {
					matchRule = getAI(owlofwarMatchInfo, listener);
					break;
				}
				return matchRule;
			}

			@Override
			public void destroyMatchInfo(MatchInfo matchInfo) {
				System.out.println("destroyMatchInfo");

			}

			@Override
			public void cancelMatch(Matchable matchable) {
				System.out.println("cancel match success");
				Role role = (Role) matchable;
				IoSession session = SessionCache.getSessionById(role.getRoleId());
				if (session != null)
					session.write(SCMessage.newBuilder().setSCMatchCancel(SCMatchCancel.newBuilder()).build());
			}

			@Override
			public void changeStartMatcher(Matchable originStarter, Matchable newStarter) {
				// TODO Auto-generated method stub

			}

			@Override
			public void waitClick(MatchInfo matchInfo, int clickCount) {
				Role role = (Role) matchInfo.getMatchRule().getMatchTarget();
				System.out.println("owlofwar match clickCount:" + clickCount + " start account:" + role.getAccount()
						+ " start name:" + role.getName() + " wait id:" + matchInfo.getMatchId());
			}
		});
	}

	@Override
	public void matchRole(IoSession session, Role role, boolean isAI, FightEventListener listener) {

		OwlofwarMatchRule matchRule = new OwlofwarMatchRule();

		matchRule.setMatchTarget(role);
		matchRule.setFightEventListener(listener);
		matchRule.setMatchNPC(isAI);
		matchRule.setPlayerCount(2);
		matchRule.setWaitTime(5);
		matchRule.setWaitUnit(TimeUnit.SECONDS);

		matchModelService.matchRole(matchRule);
	}

	private MatchRule getAI(OwlofwarMatchInfo matchInfo, FightEventListener listener) {
		Role aiRole = new Role();
		aiRole.setNpc(true);
		aiRole.setUseCardsId(1);
		// aiRole.getCardListMap().put(1,
		// AIPlayerConfigCache.getBean("ai001").getCardList());
		CardList cardList = NPCPlayerConfigFormatter.parse(NPCPlayerConfigCache.getNPCPlayerMap().get(1));
		aiRole.getCardListMap().put(1, cardList);

		matchInfo.setAIGame(true);
		if (listener != null) {
			matchInfo.setAiMapsId(listener.getAI());
			MapConfig mapsConfig = MapConfigCache.getConfigById(matchInfo.getAiMapsId());

			String name = "";
			// TODO
			if (mapsConfig != null) {
				if (mapsConfig.cardNpcType == CardNPCType.CARD_NPC_TYPE_EXTRA_CARD.getNumber()) {
					name = ExtraCardConfigCache.getExtraCardMap().get(mapsConfig.mainId).name;
				} else {
					name = CardConfigCache.getConfigById(mapsConfig.mainId).name;
				}
			}

			aiRole.setAccount(name);
			aiRole.setName(name);
		}

		OwlofwarMatchRule matchRule = new OwlofwarMatchRule();
		matchRule.setFightEventListener(null);
		matchRule.setMatchTarget(aiRole);

		return matchRule;
	}

	@Override
	public void matchRole(Role role1, FightEventListener listener1, Role role2, FightEventListener listener2) {
		// TODO Auto-generated method stub
		OwlofwarGame game = new OwlofwarGame();
		game.setControlCount(2);
		game.setTotalTime(4 * 60);

		game.setGameId(idClassCreator.getId(OwlofwarGame.class));

		Map<Integer, Role> roleMap = game.getRoleMap();
		Map<Integer, OwlofwarGameInfo> gameInfoMap = game.getRoleGameInfoMap();
		List<Integer> rolePositionList = game.getRolePositionList();

		roleMap.put(role1.getRoleId(), role1);
		roleMap.put(role2.getRoleId(), role2);

		rolePositionList.add(role1.getRoleId());
		rolePositionList.add(role2.getRoleId());

		OwlofwarGameInfo gameInfo1 = new OwlofwarGameInfo();
		gameInfo1.setRoleId(role1.getRoleId());
		gameInfoMap.put(role1.getRoleId(), gameInfo1);

		OwlofwarGameInfo gameInfo2 = new OwlofwarGameInfo();
		gameInfo2.setRoleId(role2.getRoleId());
		gameInfoMap.put(role2.getRoleId(), gameInfo2);

		role1.setOwlofwarGame(game);
		role2.setOwlofwarGame(game);

		// OwlofwarGameCache.getGameMap().put(game.getGameId(), game);

		this.matchComplete(game);
	}

	@Override
	public void matchComplete(OwlofwarGame game) {
		Map<Integer, Role> roleMap = game.getRoleMap();
		System.out.println("sendMatchComplete");
		for (Role role : roleMap.values()) {
			OwlofwarGameInfo info = game.getRoleGameInfoMap().get(role.getRoleId());
			FightEventListener listener = info.getListener();
			if (listener != null) {
				listener.matchFighter(game, listener.getRole());
			}
		}

		List<Role> roleList = new ArrayList<>(roleMap.values());
		for (int i = 0; i < roleMap.size(); i++) {
			Role tempRole = roleList.get(i);
			SCMatchComplete.Builder scMatchCompleteBuilder = SCMatchComplete.newBuilder();
			scMatchCompleteBuilder.setMapsId(game.getAiMapsId());
			for (Role role : roleMap.values()) {
				if (tempRole == role) {
					scMatchCompleteBuilder.setIndex(i);
				}

				scMatchCompleteBuilder.addMatchRoleInfos(MatchRoleInfo.newBuilder().setName(role.getName())
						.setPoint(role.getPoint())
						.setHeroId(role.isNPC() ? 0 : role.getCardListMap().get(role.getUseCardsId()).getMainId()));
			}

			SessionUtils.sc(tempRole, SCMessage.newBuilder().setSCMatchComplete(scMatchCompleteBuilder).build());
		}
	}

	private void startGame(OwlofwarGame game) {
		matchComplete(game);
	}

	// /**
	// * 匹配规则
	// *
	// * @param point
	// * @return
	// * @author wcy 2016年8月15日
	// */
	// private boolean checkCanMatchGame(OwlofwarGame game, int point, int
	// range) {
	// byte level = getLevel(point);
	//
	// byte currentGameLevel = 0;
	// // byte currentGameLevel = game.getLevel() ;
	//
	// byte delta = (byte) Math.abs(level - currentGameLevel);
	// if (!game.isMatchComplete() && delta < range) {
	// return true;
	// }
	// return false;
	// }
	//
	// private byte getLevel(int point) {
	// int level = 0;
	// Map<Integer, Integer> levelPointMap =
	// FightMapConfigCache.getLevelPointMap();
	// for (Map.Entry<Integer, Integer> entrySet : levelPointMap.entrySet()) {
	// int minPoint = entrySet.getKey();
	// if (point >= minPoint) {
	// level = entrySet.getValue();
	// break;
	// }
	// }
	// return (byte) level;
	// }

	private OwlofwarGame serverGameInit(OwlofwarMatchInfo matchInfo) {
		OwlofwarGame game = new OwlofwarGame();
		game.setControlCount(matchInfo.getMatchRule().getPlayerCount());
		game.setTotalTime(5 * 60);
		game.setGameId(idClassCreator.getId(OwlofwarGame.class));

		Map<Integer, Role> roleMap = game.getRoleMap();
		Map<Integer, OwlofwarGameInfo> gameInfoMap = game.getRoleGameInfoMap();
		List<Integer> rolePositionList = game.getRolePositionList();

		OwlofwarMatchInfo owlofwarMatchInfo = (OwlofwarMatchInfo) matchInfo;
		List<Matchable> matchables = owlofwarMatchInfo.getMatchables();
		for (Matchable matchable : matchables) {
			Role role = (Role) matchable;

			role.setOwlofwarGameId(game.getGameId());
			roleMap.put(role.getRoleId(), role);
			rolePositionList.add(role.getRoleId());

			FightEventListener listener = owlofwarMatchInfo.getFightEventListeners().get(role.getRoleId());

			OwlofwarGameInfo gameInfo = new OwlofwarGameInfo();
			gameInfo.setRoleId(role.getRoleId());
			if (listener != null) {
				gameInfo.setListener(listener);
			}
			gameInfoMap.put(role.getRoleId(), gameInfo);

			role.setOwlofwarGame(game);
			if (role.getRoleId() == 0) {
				game.setAiCount(game.getAiCount() + 1);
			}
		}

		game.setAIGame(matchInfo.isAIGame());
		game.setAiMapsId(matchInfo.getAiMapsId());

		for (Matchable matchable : matchables) {
			Role role = (Role) matchable;
			FightEventListener listener = owlofwarMatchInfo.getFightEventListeners().get(role.getRoleId());
			if (listener != null)
				listener.enterGame(role);
		}
		return game;

	}

	@Override
	public void cancelMatch(Role role) {
		matchModelService.cancelMatch(role);
	}

	@Override
	public void offline(Role role) {
		cancelMatch(role);
	}

}
