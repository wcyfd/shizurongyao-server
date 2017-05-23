package com.randioo.shizurongyao_server.formatter;

import java.util.ArrayList;
import java.util.List;

import com.randioo.shizurongyao_server.entity.bo.Card;

public class ChapterCardFormatter {
	public static List<Card> formatCard(String cardStr) {

		String[] data1 = cardStr.split(";");
		List<Card> list = new ArrayList<>(data1.length);

		for (String data2 : data1) {
			String[] data3 = data2.split(",");

			int cardId = Integer.parseInt(data3[0]);
			int num = Integer.parseInt(data3[1]);

			Card card = new Card();
			card.setCardId(cardId);
			card.setNum(num);

			list.add(card);
		}

		return list;
	}
}
