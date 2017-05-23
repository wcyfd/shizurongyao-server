package com.randioo.shizurongyao_server.entity.po;

import com.randioo.shizurongyao_server.entity.bo.Card;

public class MailCard {
	/** 卡片id */
	private int cardId;
	/** 卡片等级 */
	private int lv;

	public MailCard() {

	}

	public MailCard(Card card) {
		this.cardId = card.getCardId();
		this.lv = card.getLv();
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
	}

}
