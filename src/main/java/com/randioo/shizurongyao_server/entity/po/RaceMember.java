package com.randioo.shizurongyao_server.entity.po;

/**
 * 比赛成员
 * 
 * @author wcy 2017年5月15日
 *
 */
public class RaceMember {
	private int roleId;
	private CardList cardList = new CardList();

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public CardList getCardList() {
		return cardList;
	}

	public void setCardList(CardList cardList) {
		this.cardList.setIndex(cardList.getIndex());
		this.cardList.setMainId(cardList.getMainId());
		this.cardList.getList().addAll(cardList.getList());
	}
}
