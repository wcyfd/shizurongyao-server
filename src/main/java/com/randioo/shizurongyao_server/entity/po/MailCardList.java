package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.randioo.shizurongyao_server.entity.bo.Card;

public class MailCardList {

	/** 队长牌 */
	private MailCard mainCard = null;
	private List<MailCard> list = new ArrayList<>();

	public MailCardList() {

	}

	public MailCardList(CardList cardList1, Map<Integer, Card> cardMap) {

		this.mainCard = null;
		list.clear();

		int mainId1 = cardList1.getMainId();
		Card mainCard1 = cardMap.get(mainId1);
		MailCard mainMailCard1 = new MailCard(mainCard1);

		this.setMainCard(mainMailCard1);

		List<Integer> cardList = cardList1.getList();
		for (Integer cardId : cardList) {
			Card card = cardMap.get(cardId);
			MailCard mailCard = new MailCard(card);
			list.add(mailCard);
		}
	}

	public MailCardList(String str) {
		this.mainCard = null;
		list.clear();

		String[] data = str.split(",");
		int i = 0;
		int j = 0;
		while (i < data.length) {
			int id = Integer.parseInt(data[i++]);
			int lv = Integer.parseInt(data[i++]);

			MailCard mailCard = new MailCard();
			mailCard.setCardId(id);
			mailCard.setLv(lv);
			if (j == 0) {
				this.setMainCard(mailCard);
			} else {
				list.add(mailCard);
			}
			j++;
		}
	}

	public void setMainCard(MailCard mainCard) {
		if (mainCard == null)
			return;
		if (this.mainCard != null)
			list.remove(0);

		list.add(0, mainCard);
		this.mainCard = mainCard;

	}

	public MailCard getMainCard() {
		return mainCard;
	}

	public void addMailCard(MailCard mailCard) {
		list.add(mailCard);
	}

	public List<MailCard> getList() {
		return list;
	}

	/**
	 * cardId,lv,cardId,lv...
	 */
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (MailCard mailCard : list) {
			int lv = mailCard.getLv();
			int cardId = mailCard.getCardId();
			sb.append(cardId).append(",").append(lv).append(",");
		}
		return sb.toString();
	}
}
