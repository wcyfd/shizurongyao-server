package com.randioo.shizurongyao_server.formatter;

import java.util.ArrayList;
import java.util.List;

import com.randioo.randioo_server_base.utils.RandomUtils;
import com.randioo.shizurongyao_server.entity.po.MailCard;
import com.randioo.shizurongyao_server.entity.po.MailCardList;

public class MailCardListFormatter {
	/**
	 * cardId,lv;cardId,lv;...
	 * 
	 * @param mailCardList
	 * @return
	 * @author wcy 2016年8月10日
	 */
	public static String formatMailCardList(MailCardList mailCardList) {
		StringBuilder sb = new StringBuilder();
		if (mailCardList == null) {
			return sb.toString();
		}
		List<MailCard> list = mailCardList.getList();
		for (MailCard mailCard : list) {
			int cardId = mailCard.getCardId();
			int lv = mailCard.getLv();
			sb.append(cardId).append(",").append(lv).append(";");
		}
		return sb.toString();
	}

	/**
	 * 打乱牌组顺序
	 * 
	 * @param mailCardList
	 * @return
	 * @author wcy 2016年10月27日
	 */
	public static String formatRandomMaiLCardList(MailCardList mailCardList) {
		List<MailCard> result = new ArrayList<>();
		List<MailCard> list = new ArrayList<>(mailCardList.getList());
		while (list.size() != 0) {
			int index = RandomUtils.getRandomNum(list.size());
			result.add(list.remove(index));
		}
		StringBuilder sb = new StringBuilder();
		for (MailCard card : result) {
			sb.append(card.getCardId()).append(",").append(card.getLv()).append(",");
		}
		return sb.toString();
	}
	
	/**
	 * 随机卡牌顺序
	 * @param mailCardList
	 * @return
	 */
	public static List<MailCard> randomMailCardList(MailCardList mailCardList){
		List<MailCard> list = new ArrayList<>(mailCardList.getList().size());
		List<MailCard> temp = new ArrayList<>(mailCardList.getList());
		while(temp.size()!=0){
			int index = RandomUtils.getRandomNum(temp.size());
			list.add(temp.remove(index));
		}
		
		return list;
	}
}
