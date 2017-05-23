package com.randioo.shizurongyao_server.module.fight.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.locks.Lock;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.utils.JSInvoker;
import com.randioo.randioo_server_base.utils.game.game_type.GameBase;
import com.randioo.randioo_server_base.utils.game.game_type.GameHandler;
import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.LogicFrame;
import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.RTSGame;
import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.RTSGameStarter;
import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.RateFrameMeter;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.template.Function;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.MailCard;
import com.randioo.shizurongyao_server.entity.po.MailCardList;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGameInfo;
import com.randioo.shizurongyao_server.entity.po.Video;
import com.randioo.shizurongyao_server.entity.po.VideoManager;
import com.randioo.shizurongyao_server.formatter.MailCardListFormatter;
import com.randioo.shizurongyao_server.module.fight.FightConstant;
import com.randioo.shizurongyao_server.module.fight.FightConstant.GameFightType;
import com.randioo.shizurongyao_server.module.role.service.RoleService;
import com.randioo.shizurongyao_server.module.war.service.WarService;
import com.randioo.shizurongyao_server.protocol.Entity.FightCard;
import com.randioo.shizurongyao_server.protocol.Entity.FightType;
import com.randioo.shizurongyao_server.protocol.Entity.Frame;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Entity.GameResultAwardData;
import com.randioo.shizurongyao_server.protocol.Entity.GameResultData;
import com.randioo.shizurongyao_server.protocol.Entity.RoleResourceInfo;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.Fight.FightClientReadyResponse;
import com.randioo.shizurongyao_server.protocol.Fight.FightCountDownResponse;
import com.randioo.shizurongyao_server.protocol.Fight.FightGameActionResponse;
import com.randioo.shizurongyao_server.protocol.Fight.FightGameOverResponse;
import com.randioo.shizurongyao_server.protocol.Fight.FightGetGameAwardResponse;
import com.randioo.shizurongyao_server.protocol.Fight.FightLoadResourceProgressResponse;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightCountDown;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightGameResult;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightKeyFrame;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightLoadResource;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightLoadResourceProgress;
import com.randioo.shizurongyao_server.protocol.Fight.SCFightStartGame;
import com.randioo.shizurongyao_server.protocol.Game.GameAction;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.util.SessionUtils;

@Service("fightService")
public class FightServiceImpl extends ObserveBaseService implements FightService {

	@Autowired
	private RTSGameStarter gameStarter;

	@Autowired
	private RateFrameMeter rateFrameMeter;

	@Autowired
	private WarService warService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private JSInvoker jsInvoker;

	@Override
	public void init() {
		gameStarter.setGameHandler(new GameHandler() {

			@Override
			public void gameLogic(GameBase gameBase) {
				OwlofwarGame game = (OwlofwarGame) gameBase;
				if (game.isEnd()) {
					return;
				}
				Lock lock = game.getLock();
				lock.lock();
				try {
					if (game.isEnd()
							|| game.getCurrentFrameNumber() > game.getTotalTime() * game.getFrameCountInOneSecond()) {
						gameOver(game);
						return;
					}

					if (game.getStartTime() == 0L) {
						game.setStartTime(System.currentTimeMillis());
					}

					sendKeyFrameInfo(game);
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					lock.unlock();
				}
			}
		});
		// 设置帧频发射器的回调函数
		rateFrameMeter.setCallback(new Function() {

			@Override
			public Object apply(Object... params) {
				OwlofwarGame game = (OwlofwarGame) params[0];
				@SuppressWarnings("unchecked")
				List<LogicFrame> logicFrames = (List<LogicFrame>) params[1];

				SCFightKeyFrame.Builder message = SCFightKeyFrame.newBuilder();

				for (LogicFrame logicFrame : logicFrames) {
					boolean record = false;
					Frame.Builder frameBuilder = Frame.newBuilder();
					frameBuilder.setFrameIndex(logicFrame.getFrameIndex());
					for (Object object : logicFrame.getGameActions()) {
						GameAction gameAction = (GameAction) object;
						frameBuilder.addGameActions(gameAction);
						record = true;
					}

					Frame frame = frameBuilder.build();
					message.addFrames(frame);
					// 如果有帧事件，则记录帧
					if (record) {
						game.getVideo().getFrames().add(frame);
					}
				}

				SCMessage sc = SCMessage.newBuilder().setSCFightKeyFrame(message).build();

				// 向各用户发送消息
				for (Role role : game.getRoleMap().values()) {
					IoSession session = SessionCache.getSessionById(role.getRoleId());
					if (session != null)
						session.write(sc);
				}

				return null;
			}

		});
	}

	@Override
	public void fightInit(Role role) {

	}

	@Override
	public void gameCountDown(Role role, IoSession session) {
		session.write(SCMessage.newBuilder().setFightCountDownResponse(FightCountDownResponse.newBuilder()).build());

		OwlofwarGame game = role.getOwlofwarGame();
		if (game == null || game.isEnd()) {
			return;
		}
		game.getLock().lock();
		try {
			if (game.isEnd()) {
				return;
			}
			OwlofwarGameInfo info = game.getRoleGameInfoMap().get(role.getRoleId());
			if (info == null) {
				return;
			}

			info.setCountDown(info.getCountDown() + 1);
			// 检查两者倒计时是否相同，相同则往下走一步，否则等待（悲观帧）
			boolean isEquals = false;
			int countDown = info.getCountDown();
			for (OwlofwarGameInfo gameInfo : game.getRoleGameInfoMap().values()) {
				if (gameInfo.getRoleId() == 0) {
					// 如果是npc则直接通过
					isEquals = true;
					break;
				}
				isEquals = gameInfo.getCountDown() == countDown;
			}
			if (isEquals) {
				SCMessage sc = SCMessage.newBuilder().setSCFightCountDown(SCFightCountDown.newBuilder()).build();
				for (Role tempRole : game.getRoleMap().values()) {
					IoSession tempSession = SessionCache.getSessionById(tempRole.getRoleId());
					if (tempSession != null) {
						tempSession.write(sc);
					}
				}
			}

		} finally {
			game.getLock().unlock();
		}
	}

	/**
	 * 1.匹配完成<br>
	 * 2.客户端发送准备完成<br>
	 * 3.客户端都准备完成了,主推加载资源消息<br>
	 * 4.客户端加载资源完成发送资源加载完成协议, 在服务器第一次收到资源加载完成时开始游戏,再发过来的加载完成不予以理睬<br>
	 */

	@Override
	public void clientReady(Role role, IoSession session) {
		OwlofwarGame game = role.getOwlofwarGame();
		if (game == null || game.isEnd()) {
			session.write(SCMessage
					.newBuilder()
					.setFightClientReadyResponse(
							FightClientReadyResponse.newBuilder().setErrorCode(ErrorCode.ERR_CANCEL_ROOM.getNumber()))
					.build());
			return;
		}

		session.write(SCMessage.newBuilder().setFightClientReadyResponse(FightClientReadyResponse.newBuilder()).build());

		Lock lock = game.getLock();
		lock.lock();
		try {
			if (this.checkSomeoneOffline(game)) {
				gameOver(game);
				return;
			}
			game.getReadyRoleIdSet().add(role.getRoleId());
			try {
				// 如果玩家都准备好了，则开始加载资源
				if (game.getReadyRoleIdSet().size() + game.getAiCount() >= game.getRoleMap().size()) {
					this.loadResource(game);
				}

			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	@Override
	public void loadResource(OwlofwarGame game) {
		List<Integer> roleIdList = game.getRolePositionList();
		Map<Integer, List<MailCard>> randomCardListMap = new HashMap<>();
		Map<Integer, MailCardList> mailCardListMap = new HashMap<>();
		for (Role role : game.getRoleMap().values()) {
			MailCardList mailCardList = this.getUseCardList(role);
			randomCardListMap.put(role.getRoleId(), MailCardListFormatter.randomMailCardList(mailCardList));
			mailCardListMap.put(role.getRoleId(), mailCardList);
		}
		for (Integer roleId : roleIdList) {
			SCFightLoadResource.Builder builder = SCFightLoadResource.newBuilder().setIsNPCGame(game.isAIGame())
					.setNPCMapId(game.getAiMapsId());
			for (int i = 0, size = roleIdList.size(); i < size; i++) {
				if (roleId == roleIdList.get(i)) {
					builder.setMyPlayerId(i + 1);
				}

				Role role = game.getRoleMap().get(roleIdList.get(i));

				RoleResourceInfo.Builder roleResourceInfoBuilder = RoleResourceInfo.newBuilder()
						.setName(role.getName()).setFightPoint(0).setCorpName("").setMainLv(1).setPlayerId(i + 1)
						.setCampId(i + 1);
				OwlofwarGameInfo owlofwarGameInfo = game.getRoleGameInfoMap().get(role.getRoleId());
				// 注入战斗类型,没有则不注入
				if (owlofwarGameInfo != null) {
					FightEventListener listener = owlofwarGameInfo.getListener();
					if (listener != null) {
						GameFightType gameFightType = listener.getReturnType(role);
						if (gameFightType == GameFightType.PILLAGE) {
							roleResourceInfoBuilder.setFightType(FightType.PILLAGE);
						} else if (gameFightType == GameFightType.WAR) {
							roleResourceInfoBuilder.setFightType(FightType.WAR);
						} else if (gameFightType == GameFightType.TEST) {
							roleResourceInfoBuilder.setFightType(FightType.TEST);
						}
					}
				}
				MailCard mainCard = mailCardListMap.get(roleIdList.get(i)).getMainCard();
				roleResourceInfoBuilder.setGeneralCard(FightCard.newBuilder().setCardId(mainCard.getCardId())
						.setLv(mainCard.getLv()));
				List<MailCard> list = randomCardListMap.get(roleIdList.get(i));
				for (int index = 0; index < list.size(); index++) {
					MailCard mailCard = list.get(index);
					roleResourceInfoBuilder.addHandCards(FightCard.newBuilder().setCardId(mailCard.getCardId())
							.setLv(mailCard.getLv()));
				}

				builder.addRoleResourceInfo(roleResourceInfoBuilder);
			}

			SCFightLoadResource scFightLoadResource = builder.build();

			/** 添加录像 */
			Video video = new Video();
			video.setGameId(game.getGameId());
			game.setVideo(video);
			// VideoManager.addVideo(game.getVideo());

			// 放入资源表
			game.getVideo().getRoleResourceInfoMap().add(scFightLoadResource);

			SessionUtils.sc(roleId, SCMessage.newBuilder().setSCFightLoadResource(scFightLoadResource).build());
		}
	}

	/**
	 * 获得使用中的卡组
	 * 
	 * @param role
	 * @return
	 */
	private MailCardList getUseCardList(Role role) {

		CardList cardList = null;
		Map<Integer, Card> cardMap = null;
		MailCardList mailCardList = null;

		if (role.getRoleId() != 0) {
			// 如果不是npc
			int index = role.getUseCardsId();
			cardList = role.getCardListMap().get(index);
			cardMap = role.getCardMap();
			mailCardList = new MailCardList(cardList, cardMap);
		} else {
			// 如果是npc
			String str = "16106,1,16101,1,16102,1,16209,1,16105,1,16108,1,16301,1,16206,1,16107,1,16301,1,16103,1,16302,1";
			mailCardList = new MailCardList(str);
		}

		return mailCardList;
	}

	@Override
	public void loadingResource(Role role, int progress, boolean complete) {
		// session.write(SCMessage.newBuilder()
		// .setFightLoadResourceCompleteResponse(FightLoadResourceCompleteResponse.newBuilder()).build());
		// 回复进度
		SessionUtils.sc(
				role,
				SCMessage.newBuilder()
						.setFightLoadResourceProgressResponse(FightLoadResourceProgressResponse.newBuilder()).build());

		OwlofwarGame game = role.getOwlofwarGame();
		Set<Integer> roleIdSet = game.getRoleMap().keySet();
		Set<Integer> set = game.getResourceCompleteRoleIdSet();

		GeneratedMessage sc = SCMessage
				.newBuilder()
				.setSCFightLoadResourceProgress(
						SCFightLoadResourceProgress.newBuilder().setPlayerId(role.getRoleId())
								.setProgress(complete ? 100 : progress)).build();
		// 除了自己剩下的人通知
		for (Integer roleId : roleIdSet) {
			if (role.getRoleId() == roleId)
				continue;
			SessionUtils.sc(roleId, sc);
		}

		if (game == null || game.isEnd()) {
			return;
		}
		boolean startGame = false;
		Lock lock = game.getLock();
		lock.lock();
		try {
			if (game.isEnd())
				return;

			// 如果加载完成
			if (complete)
				set.add(role.getRoleId());

			// 双方都准备好了则开始游戏
			if (set.size() >= game.getRoleMap().size() || (game.isAIGame() && complete)) {
				startGame = true;
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

		if (startGame)
			// 如果此时有人离线，给他们获得锁的机会，让比赛不用开始
			this.startGame(game);
	}

	private void startGame(OwlofwarGame game) {
		Lock lock = game.getLock();
		if (game.isStart() || game.isEnd())
			return;
		lock.lock();
		try {
			if (game.isStart() || game.isEnd())
				return;
			game.setStart(true);

			for (Role role : game.getRoleMap().values()) {
				int roleId = role.getRoleId();
				Role otherRole = game.getAnotherRole(role);
				FightEventListener listener = game.getRoleGameInfoMap().get(roleId).getListener();
				if (listener != null) {
					listener.startFight(game, role, otherRole);
				}
			}

			for (Role x : game.getRoleMap().values()) {
				IoSession ioSession = SessionCache.getSessionById(x.getRoleId());
				if (ioSession != null) {
					ioSession.write(SCMessage.newBuilder()
							.setSCFightStartGame(SCFightStartGame.newBuilder().setTotalTime(game.getTotalTime()))
							.build());
				}
			}

			game.setAddDeltaFrame(2);
			game.setFrameCountInOneSecond(20);
			int addDeltaFrame = game.getAddDeltaFrame();
			int keyFrameDeltaTime = 1000 / ((OwlofwarGame) game).getFrameCountInOneSecond() * addDeltaFrame;

			// game.setNextFrameNumber(game.getFrameNumber() +
			// game.getAddDeltaFrame());

			gameStarter.startRTSGame(game, keyFrameDeltaTime);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public void receiveGameAction(Role role, GeneratedMessage gameActionMessage, IoSession session) {
		session.write(SCMessage.newBuilder().setFightGameActionResponse(FightGameActionResponse.newBuilder()).build());
		OwlofwarGame game = (OwlofwarGame) role.getOwlofwarGame();
		if (game == null || game.isEnd()) {
			return;
		}
		try {
			if (game.isEnd()) {
				return;
			}

			int currentFrame = this.getCurrentFrameIndex(game);

			// 将事件和当前的帧数加入到队列中，延迟执行帧数目前设为0
			game.getActionQueue().add(gameActionMessage, currentFrame, 0);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getCurrentFrameIndex(OwlofwarGame game) {
		long startTime = game.getStartTime();
		long nowTime = System.currentTimeMillis();
		// 获得游戏开始到现在的时间毫秒间隔
		long deltaTime = nowTime - startTime;
		// 获得每帧的需要的毫秒数量
		long oneFrameMilliSecond = 1000 / game.getFrameCountInOneSecond();
		// 毫秒间隔除以每帧毫秒数量去整获得第几帧
		int currentFrame = (int) (deltaTime / oneFrameMilliSecond);

		return currentFrame;
	}

	// @Override
	// public void sendKeyFrameInfo(RTSGame game) {
	// List<GameEvent> gameEvents =
	// game.getActionQueue().pollAll(game.getNextFrameNumber());
	// // 整合消息
	// SCFightKeyFrame.Builder scFightKeyFrameBuilder =
	// SCFightKeyFrame.newBuilder();
	//
	// for (int frame = game.getFrameNumber(), nextFrameNumber =
	// game.getNextFrameNumber(); frame < nextFrameNumber; frame++) {
	// Frame.Builder frameBuilder = Frame.newBuilder();
	// frameBuilder.setFrameIndex(frame);
	// // 已经加入过所以要删除的事件列表
	// List<GameEvent> deleteGameEvents = new ArrayList<>(gameEvents.size());
	// for (int j = 0, gameEventSize = gameEvents.size(); j < gameEventSize;
	// j++) {
	// GameEvent gameEvent = gameEvents.get(j);
	// int frameIndex = gameEvent.getExecuteFrameIndex();
	// if (frameIndex <= frame) {
	// GameAction gameAction = (GameAction) gameEvent.getAction();
	// frameBuilder.addGameActions(gameAction);
	//
	// deleteGameEvents.add(gameEvent);
	// }
	// }
	// for (GameEvent needDeleteGameEvent : deleteGameEvents) {
	// gameEvents.remove(needDeleteGameEvent);
	// }
	// Frame f = frameBuilder.build();
	// scFightKeyFrameBuilder.addFrames(f);
	//
	// // 记录帧
	// ((OwlofwarGame) game).getVideo().getFrames().add(f);
	// // for(OwlofwarGameInfo gameInfo
	// // :((OwlofwarGame)game).getRoleGameInfoMap().values()){
	// // gameInfo.getRunQueue().add(f);
	// // }
	// }
	//
	// game.setFrameNumber(game.getNextFrameNumber());
	// game.setNextFrameNumber(game.getNextFrameNumber() + ((OwlofwarGame)
	// game).getAddDeltaFrame());
	//
	// if (scFightKeyFrameBuilder.getFramesCount() == 0) {
	// return;
	// }
	// SCMessage sc =
	// SCMessage.newBuilder().setScFightKeyFrame(scFightKeyFrameBuilder).build();
	// // 向各用户发送消息
	// for (Role role : ((OwlofwarGame) game).getRoleMap().values()) {
	// IoSession session = SessionCache.getSessionById(role.getRoleId());
	// if (session != null)
	// session.write(sc);
	// }
	// }

	@Override
	public void sendKeyFrameInfo(RTSGame game) {
		// send1(game);
		// send2(game);
		send3(game);
	}

	// private void send1(RTSGame game) {
	// List<GameEvent> gameEvents =
	// game.getActionQueue().pollAll(game.getNextFrameNumber());
	// // 整合消息
	// SCFightKeyFrame.Builder scFightKeyFrameBuilder =
	// SCFightKeyFrame.newBuilder();
	//
	// for (int frame = game.getFrameNumber(), nextFrameNumber =
	// game.getNextFrameNumber(); frame < nextFrameNumber; frame++) {
	// Frame.Builder frameBuilder = Frame.newBuilder();
	// frameBuilder.setFrameIndex(frame);
	// // 已经加入过所以要删除的事件列表
	// List<GameEvent> deleteGameEvents = new ArrayList<>(gameEvents.size());
	// for (int j = 0, gameEventSize = gameEvents.size(); j < gameEventSize;
	// j++) {
	// GameEvent gameEvent = gameEvents.get(j);
	// int frameIndex = gameEvent.getExecuteFrameIndex();
	// if (frameIndex <= frame) {
	// GameAction gameAction = (GameAction) gameEvent.getAction();
	// frameBuilder.addGameActions(gameAction);
	//
	// deleteGameEvents.add(gameEvent);
	// }
	// }
	// for (GameEvent needDeleteGameEvent : deleteGameEvents) {
	// gameEvents.remove(needDeleteGameEvent);
	// }
	// Frame f = frameBuilder.build();
	// scFightKeyFrameBuilder.addFrames(f);
	//
	// // 记录帧
	// ((OwlofwarGame) game).getVideo().getFrames().add(f);
	// // for(OwlofwarGameInfo gameInfo
	// // :((OwlofwarGame)game).getRoleGameInfoMap().values()){
	// // gameInfo.getRunQueue().add(f);
	// // }
	// }
	//
	// game.setFrameNumber(game.getNextFrameNumber());
	// game.setNextFrameNumber(game.getNextFrameNumber() + ((OwlofwarGame)
	// game).getAddDeltaFrame());
	//
	// if (scFightKeyFrameBuilder.getFramesCount() == 0) {
	// return;
	// }
	// SCMessage sc =
	// SCMessage.newBuilder().setScFightKeyFrame(scFightKeyFrameBuilder).build();
	// // 向各用户发送消息
	// for (Role role : ((OwlofwarGame) game).getRoleMap().values()) {
	// IoSession session = SessionCache.getSessionById(role.getRoleId());
	// if (session != null)
	// session.write(sc);
	// }
	// }
	//
	// private void send2(final RTSGame game) {
	// ActionQueue actionQueue = game.getActionQueue();
	// int currentFrameNumber = game.getFrameNumber();
	// int addDeltaFrameNumber = game.getAddDeltaFrame();
	// int nextFrameNumber = game.getNextFrameNumber();
	//
	// rateFrameMeter.sendKeyFrame(actionQueue, currentFrameNumber,
	// addDeltaFrameNumber, nextFrameNumber,
	// new Function() {
	//
	// @Override
	// public Object apply(Object... params) {
	// @SuppressWarnings("unchecked")
	// List<LogicFrame> logicFrames = (List<LogicFrame>) params[0];
	// int targetFrameNumber = (int) params[1];
	// game.setFrameNumber(targetFrameNumber);
	// game.setNextFrameNumber(targetFrameNumber+game.getAddDeltaFrame());
	//
	// SCFightKeyFrame.Builder message = SCFightKeyFrame.newBuilder();
	//
	// Frame.Builder frameBuilder = Frame.newBuilder();
	// for (LogicFrame logicFrame : logicFrames) {
	// frameBuilder.setFrameIndex(logicFrame.getFrameIndex());
	// for (Object object : logicFrame.getGameActions()) {
	// GameAction gameAction = (GameAction) object;
	// frameBuilder.addGameActions(gameAction);
	// }
	//
	// message.addFrames(frameBuilder);
	// }
	//
	// SCMessage sc =
	// SCMessage.newBuilder().setScFightKeyFrame(message).build();
	//
	// // 向各用户发送消息
	// for (Role role : ((OwlofwarGame) game).getRoleMap().values()) {
	// IoSession session = SessionCache.getSessionById(role.getRoleId());
	// if (session != null)
	// session.write(sc);
	// }
	//
	// return null;
	// }
	//
	// });
	// }

	private void send3(RTSGame game) {
		rateFrameMeter.sendKeyFrame(game);
	}

	// @Override
	// public void sendKeyFrameInfo(final RTSGame game) {
	// ActionQueue actionQueue = game.getActionQueue();
	// int currentFrameNumber = game.getFrameNumber();
	// int addDeltaFrameNumber = game.getAddDeltaFrame();
	//
	// rateFrameMeter.sendKeyFrame(actionQueue, currentFrameNumber,
	// addDeltaFrameNumber, new Function() {
	//
	// @Override
	// public Object apply(Object... params) {
	// @SuppressWarnings("unchecked")
	// List<LogicFrame> logicFrames = (List<LogicFrame>) params[0];
	// int targetFrameNumber = (int) params[1];
	// game.setFrameNumber(targetFrameNumber);
	//
	// SCFightKeyFrame.Builder message = SCFightKeyFrame.newBuilder();
	//
	// Frame.Builder frameBuilder = Frame.newBuilder();
	// for (LogicFrame logicFrame : logicFrames) {
	// frameBuilder.setFrameIndex(logicFrame.getFrameIndex());
	// for (Object object : logicFrame.getGameActions()) {
	// GameAction gameAction = (GameAction) object;
	// frameBuilder.addGameActions(gameAction);
	// }
	//
	// message.addFrames(frameBuilder);
	// }
	//
	// SCMessage sc =
	// SCMessage.newBuilder().setScFightKeyFrame(message).build();
	//
	// // 向各用户发送消息
	// for (Role role : ((OwlofwarGame) game).getRoleMap().values()) {
	// IoSession session = SessionCache.getSessionById(role.getRoleId());
	// if (session != null)
	// session.write(sc);
	// }
	//
	// return null;
	// }
	//
	// });
	// }

	@Override
	public void receiveGameEnd(Role role, IoSession session, GameResult gameResult, int playerId) {
		session.write(SCMessage.newBuilder().setFightGameOverResponse(FightGameOverResponse.newBuilder()).build());
		OwlofwarGame game = role.getOwlofwarGame();

		role.setGetAward(true);
		gameOver(game);
		noticeGameResult(role.getRoleId(), gameResult, game);
	}

	private void noticeGameResult(int sendRoleId, GameResult gameResult, OwlofwarGame game) {

		List<Integer> roleIdList = game.getRolePositionList();
		Lock lock = game.getLock();
		try {
			lock.lock();
			int playerId = 0;
			for (int i = 0, size = roleIdList.size(); i < size; i++) {
				if (sendRoleId == roleIdList.get(i)) {
					playerId = i + 1;
				}
			}
			// 通知只会发送一次
			if (!game.isHasResult()) {
				SCMessage scMessage = SCMessage
						.newBuilder()
						.setSCFightGameResult(
								SCFightGameResult.newBuilder().setPlayerId(playerId).setGameResult(gameResult)).build();

				for (Role role : game.getRoleMap().values())
					SessionUtils.sc(role, scMessage);

				notifyObservers(FightConstant.NOTICE_GAME_RESULT, new ArrayList<>(game.getRoleMap().keySet()),
						sendRoleId, gameResult);

				game.setHasResult(true);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	@Override
	public GeneratedMessage getGameAward(Role role, GameResultData result) {
		GameResultAwardData.Builder gameResultAwardDataBuilder = GameResultAwardData.newBuilder();
		// 如果已经领过奖励, nb 不能重复
		if (!role.isGetAward())
			return SCMessage
					.newBuilder()
					.setFightGetGameAwardResponse(
							FightGetGameAwardResponse.newBuilder().setGameResultAwardData(gameResultAwardDataBuilder))
					.build();

		role.setGetAward(false);
		FightType fightType = result.getFightType();

		if (fightType == FightType.PILLAGE) {
			int point = this.getPillagePoint(result.getGameResult());
			roleService.addPoint(role, point);
			gameResultAwardDataBuilder.setPoint(point);
		} else if (fightType == FightType.WAR) {
			warService.refreshWarBuild(role, result, gameResultAwardDataBuilder);
		} else if (fightType == FightType.TEST) {

		}

		return SCMessage
				.newBuilder()
				.setFightGetGameAwardResponse(
						FightGetGameAwardResponse.newBuilder().setGameResultAwardData(gameResultAwardDataBuilder))
				.build();
	}

	private int getPillagePoint(GameResult gameResult) {
		return (int) (double) jsInvoker.invoke("getPillagePoint", gameResult.getNumber());
	}

	private void gameOver(OwlofwarGame game) {
		if (game == null)
			return;

		if (game.isEnd()) {
			return;
		}

		Lock lock = game.getLock();
		lock.lock();
		try {
			if (game.isEnd()) {
				return;
			}
			// 当游戏结束时就应该获得结果,前提是两者完全同步的情况下,所以只要进入过一次，剩下的所有进入都算返回false
			game.setEnd(true);
			ScheduledFuture<?> future = game.getScheduledFuture();
			if (future != null)
				future.cancel(true);

			// for (Role r : game.getRoleMap().values()) {
			// r.setOwlofwarGame(null);
			// }

			// 如果游戏有第一帧事件，则要进行视频记录
			if (game.getStartTime() != 0L) {
				Video video = game.getVideo();
				video.setStartTime(game.getStartTime());
				video.setGameId(game.getGameId());
				VideoManager.addVideo(game.getVideo());
			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
	}

	// private void sendEnd(OwlofwarGame game, Role role, byte result, int
	// roleScore1, int roleScore2) {
	// System.out.println("function sendEnd()");
	// if (this.decideResultByOnlyOneRole(game, role, result, roleScore1,
	// roleScore2)) {
	// System.out.println("receiveEnd result is:" + game.getResultMap() +
	// " score is" + game.getScoreMap());
	// Map<Integer, Byte> resultMap = game.getResultMap();
	// Map<Role, Byte> roleResultMap = new HashMap<>();
	// for (Role tempRole : game.getRoleMap().values()) {
	//
	// roleResultMap.put(tempRole, resultMap.get(tempRole.getRoleId()));
	// IoSession ioSession = SessionCache.getSessionById(tempRole.getRoleId());
	// FightEventListener listener =
	// game.getRoleGameInfoMap().get(tempRole.getRoleId()).getListener();
	// if (ioSession != null) {
	// String award = "";
	// Byte tempResult = resultMap.get(tempRole.getRoleId());
	// if (tempResult == FightConstant.WIN) {
	// if (listener != null) {
	// award = listener.getAward();
	// }
	// }
	// ioSession.write(SCMessage.newBuilder()
	// .setScFightGameOver(SCFightGameOver.newBuilder().setAward(award).setResult(tempResult))
	// .build());
	// }
	// }
	//
	// // if (!game.isAIGame()) {
	// // int roleId1 = game.getRolePositionList().get(0);
	// // int roleId2 = game.getRolePositionList().get(1);
	// // Role role1 = game.getRoleMap().get(roleId1);
	// // Role role2 = game.getRoleMap().get(roleId2);
	// // OwlofwarGameInfo info1 = game.getRoleGameInfoMap().get(roleId1);
	// // OwlofwarGameInfo info2 = game.getRoleGameInfoMap().get(roleId2);
	// //
	// // }
	// }
	//
	// }
	//
	// private boolean decideResultByOnlyOneRole(OwlofwarGame game, Role role,
	// byte result, int roleScore1, int roleScore2) {
	// System.out.println("decideResultByOnlyOneRole");
	// System.out.println("roleScore1:" + roleScore1 + " roleScore2:" +
	// roleScore2);
	// if (game == null || game.isEnd())
	// return false;
	//
	// Lock lock = game.getLock();
	// lock.lock();
	// try {
	// if (game == null || game.isEnd())
	// return false;
	// // 当游戏结束时就应该获得结果,前提是两者完全同步的情况下,所以只要进入过一次，剩下的所有进入都算返回false
	// game.getScheduledFuture().cancel(true);
	// game.setEnd(true);
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// } finally {
	// lock.unlock();
	// }
	//
	// Role role2 = game.getAnotherRole(role);
	// try {
	// if (role != null) {
	// // 如果玩家发送结果
	// // 获得双方的通信接口
	// IoSession session = SessionCache.getSessionById(role.getRoleId());
	//
	// // 赋予结果，score是比分，result是胜负
	//
	// if (session == null || !session.isConnected()) {
	// // 自己掉线了，则判定对面赢
	// this.checkCorrectResult(game, role2);
	// } else {
	// game.getScoreMap().put(role.getRoleId(), roleScore1);
	// game.getScoreMap().put(role2.getRoleId(), roleScore2);
	// game.getResultMap().put(role.getRoleId(), result);
	// game.getResultMap().put(role2.getRoleId(), (byte) -result);
	// }
	//
	// } else {
	// // 如果是游戏时间到了
	// int roleId1 = game.getRolePositionList().get(0);
	// int roleId2 = game.getRolePositionList().get(1);
	// // 获取收集到的分数信息
	// OwlofwarGameInfo info1 = game.getRoleGameInfoMap().get(roleId1);
	// OwlofwarGameInfo info2 = game.getRoleGameInfoMap().get(roleId2);
	// Map<Integer, Integer> role1ScoreInfo = info1.getScoreMap();
	// Map<Integer, Integer> role2ScoreInfo = info2.getScoreMap();
	//
	// if (game.isAIGame()) {// 如果是npc战斗
	// int score1 = this.nullThen0(role1ScoreInfo, roleId1);
	// int score2 = this.nullThen0(role1ScoreInfo, roleId2);
	// if (score1 > score2) {
	// result = FightConstant.WIN;
	// } else if (score1 == score2) {
	// result = FightConstant.DOGFALL;
	// } else {
	// result = FightConstant.LOSS;
	// }
	// game.getScoreMap().put(roleId1, score1);
	// game.getScoreMap().put(roleId2, score2);
	// game.getResultMap().put(roleId1, result);
	// game.getResultMap().put(roleId2, (byte) -result);
	// } else {
	// // 如果不是npc战斗
	// int role1Score1 = this.nullThen0(role1ScoreInfo, roleId1);
	// int role1Score2 = this.nullThen0(role1ScoreInfo, roleId2);
	// int role2Score1 = this.nullThen0(role2ScoreInfo, roleId1);
	// int role2Score2 = this.nullThen0(role2ScoreInfo, roleId2);
	// if (role1Score1 != role2Score1 || role1Score2 != role2Score2) {
	// this.checkCorrectResult(game, role);
	// }
	// }
	// }
	//
	// } catch (Exception e) {
	// e.printStackTrace();
	// }
	//
	// return true;
	//
	// }
	//
	// private int nullThen0(Map<Integer, Integer> map, int key) {
	// Integer value = map.get(key);
	// if (value == null) {
	// value = 0;
	// map.put(key, value);
	// }
	// return value;
	// }

	// private void directReceiveResult(OwlofwarGame game, Role role) {
	// Map<Integer, Byte> receiveResultMap = game.getReceiveResultMap();
	// Map<Integer, Byte> resultMap = game.getResultMap();
	// for (Entry<Integer, Byte> entrySet : receiveResultMap.entrySet()) {
	// resultMap.put(entrySet.getKey(), entrySet.getValue());
	// }
	// // win loss or dogfall
	// Map<Integer, Integer> scoreMap =
	// game.getRoleGameInfoMap().get(role.getRoleId()).getScoreMap();
	// for (Map.Entry<Integer, Integer> entrySet : scoreMap.entrySet()) {
	// game.getScoreMap().put(entrySet.getKey(), entrySet.getValue());
	// }
	// }

	// private void checkCorrectResult(OwlofwarGame game, Role role) {
	// // this.directReceiveResult(game, role);
	// System.out.println("hasCalculate");
	// }

	@Override
	public void offline(Role role, GameResult gameResult) {
		OwlofwarGame game = role.getOwlofwarGame();
		if (game == null || game.isEnd()) {
			return;
		}
		Lock lock = game.getLock();
		try {
			lock.lock();
			if (game.isEnd()) {
				return;
			}

			gameOver(game);

			noticeGameResult(role.getRoleId(), gameResult, game);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}

	}

	/**
	 * 检查是否有人下线
	 * 
	 * @param game
	 * @return
	 * @author wcy 2016年12月7日
	 */
	private boolean checkSomeoneOffline(OwlofwarGame game) {
		boolean hasOffline = false;
		if (game == null || game.isEnd()) {
			return true;
		}
		Lock lock = game.getLock();
		lock.lock();
		try {
			if (game.isEnd()) {
				return true;
			}
			// 如果有人已经离线了，则直接返回
			Map<Integer, Role> roleMap = game.getRoleMap();
			for (Role r : roleMap.values()) {
				if (r.getOwlofwarGame() == null) {
					hasOffline = true;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			lock.unlock();
		}
		return hasOffline;
	}

}
