package com.randioo.shizurongyao_server.module.fight.service;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.game.game_type.real_time_strategy_game.RTSGame;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Entity.GameResultData;

public interface FightService extends ObserveBaseServiceInterface {
	/**
	 * 客户端加载完毕
	 * 
	 * @param role
	 * @return
	 */
	public void clientReady(Role role, IoSession session);

	void loadResource(OwlofwarGame game);

	void loadingResource(Role role, int progress, boolean complete);

	public void receiveGameAction(Role role, GeneratedMessage message, IoSession session);

	void receiveGameEnd(Role role, IoSession session, GameResult gameResult, int playerId);

	/**
	 * 对战时掉线处理
	 * 
	 * @param role
	 * @author wcy 2016年8月23日
	 */
	void offline(Role role, GameResult gameResult);

	void sendKeyFrameInfo(RTSGame game);

	/**
	 * 获得比赛奖励
	 * 
	 * @param gameId
	 * @param role
	 * @param result
	 * @param score
	 * @author wcy 2016年12月6日
	 */
	GeneratedMessage getGameAward(Role role, GameResultData result);

	/**
	 * 游戏倒计时
	 * 
	 * @param role
	 * @param session
	 * @author wcy 2016年12月30日
	 */
	void gameCountDown(Role role, IoSession session);

	void fightInit(Role role);

}
