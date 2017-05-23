package com.randioo.shizurongyao_server.formatter;

import java.util.List;

import com.randioo.shizurongyao_server.entity.file.NPCPlayerConfig;
import com.randioo.shizurongyao_server.entity.po.CardList;

public class NPCPlayerConfigFormatter {

	public static CardList parse(List<NPCPlayerConfig> list) {

		// CardList cardList = new CardList();
		// cardList.setIndex(0);
		// String[] tempStr = this.cards.split(",");
		//
		// cardList.setMainId(Integer.parseInt(tempStr[0]));
		// for(int i= 1 ; i < tempStr.length;i++)
		// {
		// if(!tempStr[i].equals(""))
		// {
		// cardList.getList().add(Integer.parseInt(tempStr[i]));
		// }
		// }
		//
		// return cardList;

		CardList cardList = new CardList();
		cardList.setIndex(0);

		cardList.setMainId(list.get(0).cardId);
		for (int i = 1; i < list.size(); i++) {
			cardList.getList().add(list.get(i).cardId);
		}
		return cardList;

	}
}
