package com.randioo.shizurongyao_server.cache.file;

import java.util.ArrayList;
import java.util.List;

import com.randioo.shizurongyao_server.entity.file.CardInitConfig;

public class CardInitConfigCache {

	private static List<CardInitConfig> cardInitList = new ArrayList<>();

	public static void putConfig(CardInitConfig config) {
		cardInitList.add(config);
	}

	public static List<CardInitConfig> getCardInitList() {
		return cardInitList;
	}

}
