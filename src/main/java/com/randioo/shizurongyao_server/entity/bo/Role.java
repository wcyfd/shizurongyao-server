package com.randioo.shizurongyao_server.entity.bo;

import java.util.HashMap;
import java.util.Map;

import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.entity.po.Market;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.entity.po.War;

public class Role extends GameRole {

	/** 银币 */
	private int money;
	/** 金币 */
	private int gold;
	/** 粮食 */
	private int food;
	/** 积分 */
	private int point;
	/** 现有卡集合 */
	private Map<Integer, Card> cardMap = new HashMap<Integer, Card>();
	/** 卡组 */
	private Map<Integer, CardList> cardListMap = new HashMap<Integer, CardList>();
	/** 原始卡牌字符串 */
	private String rawListStr;
	/** 卡牌字符串 */
	private String listStr;
	/** 当前使用中的卡组ID */
	private int useCardsId;

	/** 战斗世界 */
	private OwlofwarGame owlofwarGame;
	/** 游戏id */
	private int owlofwarGameId;
	/** 商城 */
	private Market market;
	/** 战场 */
	private War war;
	/** 当前打的章节 */
	private int currentChapterId;

	private boolean isGetAward;
	/** 重命名次数 */
	private int renameCount;	
	/**胜场*/
	private int winCount;

	public void setMarket(Market market) {
		this.market = market;
	}

	public Market getMarket() {
		return market;
	}

	public War getWar() {
		return war;
	}

	public void setWar(War war) {
		this.war = war;
	}

	public void setOwlofwarGame(OwlofwarGame owlofwarGame) {
		this.owlofwarGame = owlofwarGame;
	}

	public OwlofwarGame getOwlofwarGame() {
		return owlofwarGame;
	}

	public int getMoney() {
		return money;
	}

	public void setMoney(int money) {
		setChange(true);
		this.money = money;
	}

	public int getGold() {
		return gold;
	}

	public void setGold(int gold) {
		setChange(true);
		this.gold = gold;
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		setChange(true);
		this.food = food;
	}

	public Map<Integer, Card> getCardMap() {
		return cardMap;
	}

	public Map<Integer, CardList> getCardListMap() {
		return cardListMap;
	}

	public String getListStr() {
		StringBuilder sb = new StringBuilder();
		for (CardList x : cardListMap.values()) {
			sb.append(x.getIndex()).append(",").append(x.getMainId()).append(",");
			int count = 0;
			for (Integer z : x.getList()) {
				count++;
				if (count >= x.getList().size()) {
					sb.append(z).append(";");
				} else {
					sb.append(z).append(",");
				}
			}
		}
		this.listStr = sb.toString();
		return listStr;
	}

	public void setListStr(String listStr) {
		if (listStr == null || listStr.equals(""))
			return;
		String str[] = listStr.split(";");
		for (String x : str) {
			CardList temp = new CardList(x);
			this.cardListMap.put(temp.getIndex(), temp);
		}

	}

	public void setRawListStr(String listStr) {
		if (listStr == null || listStr.equals("")) {
			return;
		}
		this.rawListStr = listStr;
		this.setListStr(listStr);
	}

	public int getUseCardsId() {
		return useCardsId;
	}

	public void setUseCardsId(int useCardsId) {
		setChange(true);
		this.useCardsId = useCardsId;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		setChange(true);
		this.point = point;
	}

	public int getOwlofwarGameId() {
		return owlofwarGameId;
	}

	public void setOwlofwarGameId(int owlofwarGameId) {
		this.owlofwarGameId = owlofwarGameId;
	}

	@Override
	public boolean checkChange() {
		return StringUtils.checkChange(rawListStr, getListStr());
	}

	public int getCurrentChapterId() {
		return currentChapterId;
	}

	public void setCurrentChapterId(int currentChapterId) {
		setChange(true);
		this.currentChapterId = currentChapterId;
	}

	public boolean isGetAward() {
		return isGetAward;
	}

	public void setGetAward(boolean isGetAward) {
		this.isGetAward = isGetAward;
	}

	public int getRenameCount() {
		return renameCount;
	}

	public void setRenameCount(int renameCount) {
		setChange(true);
		this.renameCount = renameCount;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		setChange(true);
		this.winCount = winCount;
	}

}
