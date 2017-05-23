package com.randioo.shizurongyao_server.module.card.service;

import java.util.Map;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.CardList;

public interface CardService extends ObserveBaseServiceInterface {

	/**
	 * 随机玩家卡组
	 * 
	 * @param role
	 */
	public void randomCardList(Role role, int i);

	/**
	 * 获取卡组信息
	 * 
	 * @param role
	 * @return
	 */
	public GeneratedMessage getInfoCards(Role role);

	/**
	 * 编辑卡组
	 * 
	 * @param role
	 * @param index
	 * @param cardId
	 * @return
	 */
	public GeneratedMessage changeCards(Role role, int id, int index, int cardId);

	/**
	 * 随机主将
	 * 
	 * @param map
	 * @return
	 */
	public int randomMainId(Map<Integer, Card> map);

	/**
	 * 卡升级
	 * 
	 * @param role
	 * @param cardId
	 * @return
	 */
	public GeneratedMessage cardLvUp(Role role, int cardId);

	/**
	 * 检查卡牌数量是否允许使用
	 * 
	 * @param cardList
	 * @param cardId
	 * @param config
	 * @return
	 * @author wcy 2016年5月30日
	 */
	boolean checkCardNum(CardList cardList, int cardId, int useTime);

	/**
	 * 选择卡组
	 * 
	 * @param role
	 * @param index
	 * @return
	 * @author wcy 2016年7月7日
	 */
	GeneratedMessage chooseUseCardList(Role role, int index);


	/**
	 * 创建卡牌
	 * 
	 * @param role
	 * @param cardId
	 * @return
	 */
	Card getCard(Role role, int cardId);

	GeneratedMessage changeHero(Role role, int useCardListIndex, int cardId);

	void cardInit(Role role);

	void newRoleInit(Role role);

}
