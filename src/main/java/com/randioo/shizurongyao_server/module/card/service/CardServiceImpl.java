package com.randioo.shizurongyao_server.module.card.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.file.CardConfigCache;
import com.randioo.shizurongyao_server.cache.file.CardInitConfigCache;
import com.randioo.shizurongyao_server.cache.file.CardLevelConfigCache;
import com.randioo.shizurongyao_server.dao.CardDao;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.file.CardConfig;
import com.randioo.shizurongyao_server.entity.file.CardInitConfig;
import com.randioo.shizurongyao_server.entity.file.CardLevelConfig;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.module.login.LoginConstant;
import com.randioo.shizurongyao_server.protocol.Card.CardChangeMainCardResponse;
import com.randioo.shizurongyao_server.protocol.Card.CardChooseUseCardListResponse;
import com.randioo.shizurongyao_server.protocol.Card.CardEditCardListResponse;
import com.randioo.shizurongyao_server.protocol.Card.CardGetCardsInfoResponse;
import com.randioo.shizurongyao_server.protocol.Card.CardLvUpResponse;
import com.randioo.shizurongyao_server.protocol.Entity.CardListData;
import com.randioo.shizurongyao_server.protocol.Entity.CardType;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;

@Service("cardService")
public class CardServiceImpl extends ObserveBaseService implements CardService {
	@Autowired
	private CardDao cardDao;

	@Autowired
	private GameDB gameDB;

	// @Override
	// public void newRoleInit(Role role) {
	// for (Integer x : CardInitConfigCache.getList()) {
	// Card temp = new Card();
	// temp.setLv((byte) 1);
	// temp.setCardId(x);
	// temp.setNum(1);
	// temp.setRoleId(role.getRoleId());
	// role.getCardMap().put(temp.getCardId(), temp);
	// gameDB.getInsertPool().submit(new DBRunnable<Card>(temp) {
	//
	// @Override
	// public void run(Card card) {
	// // TODO Auto-generated method stub
	// cardDao.insertCard(card);
	// }
	// });
	// }
	// // // 此时随机 完成卡组生成
	// // for (int i = 1; i <= 3; i++) {
	// // cardService.randomCardList(role, i);
	// // }
	//
	// for (int i = 1; i <= 3; i++) {
	// List<Integer> cardIntegerList = CardInitConfigCache.getList();
	// CardList cardList = new CardList();
	// cardList.setIndex(i);
	// for (int j = 0; j < LoginConstant.CARDLIST_SIZE; j++) {
	// if (j == 0) {
	// cardList.setMainId(cardIntegerList.get(0));
	// } else {
	// cardList.getList().add(cardIntegerList.get(j));
	// }
	// }
	// role.getCardListMap().put(i, cardList);
	// }
	//
	// role.setUseCardsId(1);
	// }
	@Override
	public void newRoleInit(Role role) {

		for (CardInitConfig config : CardInitConfigCache.getCardInitList()) {
			Card card = getCard(role, config.cardId);
			card.setNum(1);
		}

		for (int i = 1; i <= 3; i++) {
			List<CardInitConfig> list = CardInitConfigCache.getCardInitList();
			CardList cardList = new CardList();
			cardList.setIndex(i);
			for (int index = 0; index < LoginConstant.CARDLIST_SIZE; index++) {
				CardInitConfig config = list.get(index);
				if (index == 0) {
					cardList.setMainId(config.cardId);
				} else {
					cardList.getList().add(config.cardId);
				}
			}

			role.getCardListMap().put(i, cardList);
		}
		role.setUseCardsId(1);

	}

	@Override
	public void cardInit(Role role) {
		// 初始化卡组信息
		List<Card> list = cardDao.selectList(role.getRoleId());
		for (Card card : list) {
			role.getCardMap().put(card.getCardId(), card);
		}
	}

	public void randomCardList(Role role, int i) {
		CardList cardList = new CardList();
		cardList.setIndex(i);

		// 随机主将
		cardList.setMainId(this.randomMainId(role.getCardMap()));
		// 随机卡组 主将卡已经无法使用 英雄只能随机一次，其他卡可以随机多次
		Map<Integer, Integer> count = new HashMap<Integer, Integer>();
		List<Card> randomList = new ArrayList<Card>();
		for (Card x : role.getCardMap().values()) {
			if (x.getLv() >= 1) {
				// 解锁的武将才可用
				randomList.add(x);
			}
		}
		Random r = new Random();
		for (int j = 0; j < 7; j++) {
			int use = r.nextInt(randomList.size());
			int useId = randomList.get(use).getCardId();
			// 判断是否合法
			if (useId == cardList.getMainId()) {
				j--;
				continue;
			}
			// 判断卡类型
			// CardConfig config = CardConfigCache.getConfigById(useId);
			CardConfig config = CardConfigCache.getConfigById(useId);
			if (count.get(useId) == null) {
				cardList.getList().add(useId);
				count.put(useId, 1);
			} else if (count.get(useId) != null && count.get(useId) < config.useTimes) {
				cardList.getList().add(useId);
				int time = count.get(useId);
				count.put(useId, time + 1);
			} else {
				j--;
			}
		}
		role.getCardListMap().put(cardList.getIndex(), cardList);
	}

	@Override
	public GeneratedMessage getInfoCards(Role role) {
		CardGetCardsInfoResponse.Builder cardGetCardsInfoResponseBuilder = CardGetCardsInfoResponse.newBuilder();
		for (CardList x : role.getCardListMap().values()) {
			cardGetCardsInfoResponseBuilder.addCardListDatas(CardListData.newBuilder().setMainId(x.getMainId())
					.setCardListIndex(x.getIndex()).addAllCardIds(x.getList()));
		}

		return SCMessage.newBuilder()
				.setCardGetCardsInfoResponse(cardGetCardsInfoResponseBuilder.setUseCardId(role.getUseCardsId()))
				.build();
	}

	@Override
	public GeneratedMessage changeCards(Role role, int id, int index, int cardId) {
		// 取出卡组
		CardList cardList = role.getCardListMap().get(id);
		if (cardList == null)
			return null;
		// 判断是否 拥有
		if (!role.getCardMap().containsKey(cardId)) {

			return SCMessage
					.newBuilder()
					.setCardEditCardListResponse(
							CardEditCardListResponse.newBuilder().setErrorCode(ErrorCode.NO_CARD.getNumber())).build();
		}
		if (index > 11) {
			return SCMessage
					.newBuilder()
					.setCardEditCardListResponse(
							CardEditCardListResponse.newBuilder().setErrorCode(ErrorCode.OUT_INDEX_OF.getNumber()))
					.build();
		}

		// 开始过滤合法性
		// CardConfig config = CardConfigCache.getConfigById(cardId);
		CardConfig config = CardConfigCache.getConfigById(cardId);
		// 记录替换的卡 是否出现在卡组中
		int oldCard;
		if (index == 0) {
			oldCard = cardList.getMainId();
			// 更换主将
			if (config.cardType != CardType.CARD_TYPE_HERO.getNumber()) {
				return SCMessage
						.newBuilder()
						.setCardEditCardListResponse(
								CardEditCardListResponse.newBuilder().setErrorCode(
										ErrorCode.ERR_TYPE_MAIN_ID.getNumber())).build();
			}
			// 判断替换卡是否出现在卡组中

			if (cardList.getList().contains(cardId)) {
				int oldIndex = cardList.getList().indexOf(cardId);
				// 将旧主将替换该位置
				cardList.getList().set(oldIndex, oldCard);
			}
			// 替换新 主将
			cardList.setMainId(cardId);
		} else {
			// 替换卡组
			index = index - 1;
			// 检查新卡是否超过上限
			if (cardList.getMainId() == cardId) {
				return SCMessage
						.newBuilder()
						.setCardEditCardListResponse(
								CardEditCardListResponse.newBuilder()
										.setErrorCode(ErrorCode.ERR_NUM_CARD_H.getNumber())).build();
			}
			if (!this.checkCardNum(cardList, cardId, config.useTimes)) {
				return SCMessage
						.newBuilder()
						.setCardEditCardListResponse(
								CardEditCardListResponse.newBuilder()
										.setErrorCode(ErrorCode.ERR_NUM_CARD_N.getNumber())).build();
			}
			cardList.getList().set(index, cardId);
		}

		return SCMessage
				.newBuilder()
				.setCardEditCardListResponse(
						CardEditCardListResponse.newBuilder().setCardListData(
								CardListData.newBuilder().setMainId(cardList.getMainId())
										.setCardListIndex(cardList.getIndex()).addAllCardIds(cardList.getList())))
				.build();
	}

	@Override
	public GeneratedMessage cardLvUp(Role role, int cardId) {
		Card card = role.getCardMap().get(cardId);
		if (card == null)
			return null;
		// CardConfig config = CardConfigCache.getConfigById(cardId);
		CardConfig config = CardConfigCache.getConfigById(cardId);
		// CardLevel cardLevel =
		// CardLevelConfigCache.getConfig(config.getQuality(), card.getLv());
		CardLevelConfig cardLevel = CardLevelConfigCache.getByQualityAndLevel(config.quality, card.getLv());
		if (cardLevel == null) {
			return null;
		}

		if (cardLevel.money == 0) {
			return SCMessage.newBuilder()
					.setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.MAX_CARD_LV.getNumber()))
					.build();
		}

		if (role.getMoney() < cardLevel.money) {
			return SCMessage.newBuilder()
					.setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY.getNumber()))
					.build();
		}

		if (card.getNum() < cardLevel.levelUpNeedCard) {
			return SCMessage.newBuilder()
					.setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.NO_CARD.getNumber()))
					.build();
		}
		card.setLv(card.getLv() + 1);
		card.setNum(card.getNum() - cardLevel.levelUpNeedCard);
		// roleService.addMoney(role, -cardLevel.getCostMoney());

		return SCMessage.newBuilder().setCardLvUpResponse(CardLvUpResponse.newBuilder().setCardId(cardId)).build();

	}

	// @Override
	// public GeneratedMessage cardLvUp(Role role, int cardId) {
	// Card card = role.getCardMap().get(cardId);
	// if (card == null)
	// return null;
	// CardConfig config = CardConfigCache.getConfigById(cardId);
	// CardLevel cardLevel = CardLevelConfigCache.getConfig(config.getQuality(),
	// card.getLv());
	// if (cardLevel == null) {
	// return null;
	// }
	//
	// if (cardLevel.getCostMoney() == 0) {
	// return SCMessage.newBuilder()
	// .setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.MAX_CARD_LV)).build();
	// }
	//
	// if (role.getMoney() < cardLevel.getCostMoney()) {
	// return SCMessage.newBuilder()
	// .setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.NO_MONEY)).build();
	// }
	//
	// if (card.getNum() < cardLevel.getCostCard()) {
	// return SCMessage.newBuilder()
	// .setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.NO_CARD)).build();
	// }
	// card.setLv((byte) (card.getLv() + 1));
	// card.setNum(card.getNum() - cardLevel.getCostCard());
	// // roleService.addMoney(role, -cardLevel.getCostMoney());
	//
	// return SCMessage.newBuilder()
	// .setCardLvUpResponse(CardLvUpResponse.newBuilder().setErrorCode(ErrorCode.SUCCESS).setCardId(cardId))
	// .build();
	//
	// }

	@Override
	public GeneratedMessage chooseUseCardList(Role role, int index) {

		role.setUseCardsId(index);
		return SCMessage.newBuilder().setCardChooseUseCardListResponse(CardChooseUseCardListResponse.newBuilder())
				.build();
	}

	public int randomMainId(Map<Integer, Card> map) {
		List<Integer> mainList = new ArrayList<Integer>();
		for (Card x : map.values()) {
			// CardConfig config = CardConfigCache.getConfigById(x.getCardId());
			CardConfig config = CardConfigCache.getConfigById(x.getCardId());
			if (config.cardType == CardType.CARD_TYPE_HERO.getNumber()) {
				mainList.add(x.getCardId());
			}
		}
		if (mainList.size() == 0) {
			return -1;
		}
		Random r = new Random();
		int rr = r.nextInt(mainList.size());
		return mainList.get(rr);

	}

	@Override
	public boolean checkCardNum(CardList cardList, int cardId, int useTime) {
		boolean flag = false;
		int count = 0;

		for (Integer x : cardList.getList()) {
			if (x == cardId)
				count++;
		}

		if (count < useTime) {
			flag = true;
		}
		return flag;
	}

	@Override
	public Card getCard(Role role, int cardId) {
		Card card = role.getCardMap().get(cardId);
		if (card == null) {
			card = new Card();
			card.setCardId(cardId);
			card.setLv(1);
			card.setNum(0);
			card.setRoleId(role.getRoleId());
			role.getCardMap().put(card.getCardId(), card);
			gameDB.getInsertPool().submit(new DBRunnable<Card>(card) {

				@Override
				public void run(Card entity) {
					cardDao.insert(entity);
				}
			});
		}

		return card;
	}

	@Override
	public GeneratedMessage changeHero(Role role, int useCardListIndex, int cardId) {
		CardList cardList = role.getCardListMap().get(useCardListIndex);
		// CardConfig config = CardConfigCache.getConfigById(cardId);
		CardConfig config = CardConfigCache.getConfigById(cardId);
		if (config.cardType != CardType.CARD_TYPE_HERO.getNumber()) {
			return SCMessage
					.newBuilder()
					.setCardChangeMainCardResponse(
							CardChangeMainCardResponse.newBuilder()
									.setErrorCode(ErrorCode.ERR_TYPE_MAIN_ID.getNumber())).build();

		}
		int mainId = cardList.getMainId();
		if (cardId == mainId) {
			return SCMessage.newBuilder().setCardChangeMainCardResponse(CardChangeMainCardResponse.newBuilder())
					.build();

		}

		List<Integer> list = cardList.getList();
		int index = 0;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i) == cardId) {
				index = i;
				break;
			}
		}

		list.remove(index);
		list.add(index, mainId);
		cardList.setMainId(cardId);

		return SCMessage.newBuilder().setCardChangeMainCardResponse(CardChangeMainCardResponse.newBuilder()).build();

	}

}
