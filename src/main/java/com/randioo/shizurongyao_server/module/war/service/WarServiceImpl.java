package com.randioo.shizurongyao_server.module.war.service;

import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.file.WarBuildConfigCache;
import com.randioo.shizurongyao_server.cache.file.WarChapterConfigCache;
import com.randioo.shizurongyao_server.dao.WarBuildDao;
import com.randioo.shizurongyao_server.dao.WarChapterDao;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.bo.WarBuild;
import com.randioo.shizurongyao_server.entity.bo.WarChapter;
import com.randioo.shizurongyao_server.entity.po.FightEventListener;
import com.randioo.shizurongyao_server.entity.po.FightEventListenerAdapter;
import com.randioo.shizurongyao_server.entity.po.Market;
import com.randioo.shizurongyao_server.entity.po.MarketItem;
import com.randioo.shizurongyao_server.entity.po.War;
import com.randioo.shizurongyao_server.formatter.ChapterCardFormatter;
import com.randioo.shizurongyao_server.module.card.service.CardService;
import com.randioo.shizurongyao_server.module.fight.FightConstant.GameFightType;
import com.randioo.shizurongyao_server.module.market.MarketConstant.MarketBuyType;
import com.randioo.shizurongyao_server.module.match.service.MatchService;
import com.randioo.shizurongyao_server.module.role.service.RoleService;
import com.randioo.shizurongyao_server.module.war.WarConstant;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;
import com.randioo.shizurongyao_server.protocol.Entity.GameResultAwardData;
import com.randioo.shizurongyao_server.protocol.Entity.GameResultData;
import com.randioo.shizurongyao_server.protocol.Entity.WarBuildData;
import com.randioo.shizurongyao_server.protocol.Entity.WarChapterData;
import com.randioo.shizurongyao_server.protocol.Entity.WarChapterData.ChapterAwardState;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.protocol.War.WarGetChapterAwardResponse;
import com.randioo.shizurongyao_server.protocol.War.WarMarchResponse;
import com.randioo.shizurongyao_server.protocol.War.WarShowWarBuildResponse;
import com.randioo.shizurongyao_server.protocol.War.WarShowWarChapterResponse;

@Service("warService")
public class WarServiceImpl extends ObserveBaseService implements WarService {

	@Autowired
	private WarBuildDao warBuildDao;
	@Autowired
	private WarChapterDao warChapterDao;
	@Autowired
	private MatchService matchService;
	@Autowired
	private RoleService roleService;
	@Autowired
	private CardService cardService;

	@Override
	public void newRoleInit(Role role) {
		War war = new War();
		role.setWar(war);
		war.setRoleId(role.getRoleId());
	}

	@Override
	public void warInit(Role role) {
		// 初始化战场信息
		War war = new War();
		role.setWar(war);
		war.setRoleId(role.getRoleId());

		List<WarBuild> warBuildList = warBuildDao.selectList(role.getRoleId());
		List<WarChapter> warChapterList = warChapterDao.selectList(role.getRoleId());

		for (WarBuild warBuild : warBuildList) {
			war.getWarBuildMap().put(warBuild.getBuildId(), warBuild);
		}

		for (WarChapter warChapter : warChapterList) {
			war.getWarChapterMap().put(warChapter.getChapterId(), warChapter);
		}
	}

	@Override
	public GeneratedMessage showWarChapterInfo(Role role) {
		War war = role.getWar();
		// 章节列表
		Map<Integer, WarChapter> chapterMap = war.getWarChapterMap();
		// 建筑列表
		Map<Integer, WarBuild> warBuildMap = war.getWarBuildMap();
		WarShowWarChapterResponse.Builder warShowWarChapterResponseBuilder = WarShowWarChapterResponse.newBuilder()
		// 设置当前所在章节信息
				.setCurrentChapterId(role.getCurrentChapterId());
		// 当前所在章节的星数
		for (WarChapter warChapter : chapterMap.values()) {
			int chapterId = warChapter.getChapterId();
			// 根据章节获得建筑列表
			List<Integer> warBuildIdList = this.getWarBuildByChapterId(chapterId);
			int totalStar = 0;
			for (Integer warBuildId : warBuildIdList) {
				WarBuild warBuild = warBuildMap.get(warBuildId);
				// 如果没有打过该建筑，或该建筑的星数为零则跳过
				if (warBuild == null || warBuild.getStarCount() == 0)
					continue;
				int starCount = warBuild.getStarCount();
				totalStar += starCount;
			}

			WarChapterData warChapterData = WarChapterData.newBuilder().setChapterId(warChapter.getChapterId())
					.setStarCount(totalStar)
					.setState(this.getChapterAwardState(this.canGetChapterAward(role, chapterId))).build();
			warShowWarChapterResponseBuilder.addWarChapterData(warChapterData);
		}

		List<Integer> chapterIdList = this.getChapterId();
		int newestChapterId = chapterIdList.get(0);
		// 先找到当前的最新章节
		for (Integer chapterId : chapterIdList) {
			if (chapterMap.get(chapterId) != null) {
				newestChapterId = chapterId;
			} else {
				break;
			}
		}

		// 检查最新章节是否已经通关
		boolean isPass = true;
		List<Integer> buildIdList = this.getWarBuildByChapterId(newestChapterId);
		// 只检查最后一个建筑
		int buildId = buildIdList.get(buildIdList.size() - 1);
		WarBuild warBuild = warBuildMap.get(buildId);
		if (warBuild == null || warBuild.getStarCount() == 0) {
			isPass = false;
		}

		if (isPass) {
			// 如果最新的已经通关则显示下一章，如果没有下一章则显示-1
			int index = chapterIdList.indexOf(newestChapterId);
			newestChapterId = index >= chapterIdList.size() - 1 ? -1 : chapterIdList.get(index + 1);
		}

		return SCMessage.newBuilder()
				.setWarShowWarChapterResponse(warShowWarChapterResponseBuilder.setNextChapterId(newestChapterId))
				.build();
	}

	@Override
	public GeneratedMessage showWarBuildInfo(Role role, int chapterId) {
		War war = role.getWar();
		Map<Integer, WarBuild> warBuildMap = war.getWarBuildMap();
		// 如果等于0则是当前的章节id
		if (chapterId == 0) {
			chapterId = role.getCurrentChapterId();
		}
		List<Integer> warBuildIdList = this.getWarBuildByChapterId(chapterId);
		WarShowWarBuildResponse.Builder warShowWarBuildResponseBuilder = WarShowWarBuildResponse.newBuilder();
		// 当前章节的星数
		int chapterStarCount = 0;
		for (Integer buildId : warBuildIdList) {
			WarBuild warBuild = warBuildMap.get(buildId);
			// 如果没有打过该建筑，或该建筑的星数为零则跳过
			if (warBuild == null || warBuild.getStarCount() == 0)
				continue;
			WarBuildData warBuildData = WarBuildData.newBuilder().setStarCount(warBuild.getStarCount())
					.setBuildId(buildId).build();
			warShowWarBuildResponseBuilder.addWarBuildData(warBuildData);
			chapterStarCount += warBuild.getStarCount();
		}

		// 记录章节位置
		role.setCurrentChapterId(chapterId);
		WarChapterData currentChapterData = WarChapterData.newBuilder().setChapterId(chapterId)
				.setStarCount(chapterStarCount)
				.setState(this.getChapterAwardState(this.canGetChapterAward(role, chapterId))).build();
		warShowWarBuildResponseBuilder.setCurrentChapterData(currentChapterData);

		return SCMessage.newBuilder().setWarShowWarBuildResponse(warShowWarBuildResponseBuilder).build();
	}

	@Override
	public void march(Role role, int buildId, IoSession session) {
		int chapterId = this.getChapterIdByBuildId(buildId);
		if (!this.checkBuild(role, chapterId, buildId)) {
			session.write(SCMessage.newBuilder()
					.setWarMarchResponse(WarMarchResponse.newBuilder().setErrorCode(ErrorCode.NO_MARCH_TARGET.getNumber())).build());
			return;
		}

		Map<Integer, WarChapter> warChapterMap = role.getWar().getWarChapterMap();

		// 如果没有这个章节，则新建
		WarChapter warChapter = warChapterMap.get(chapterId);
		if (warChapter == null) {
			warChapter = new WarChapter();
			warChapter.setAward(0);
			warChapter.setChapterId(chapterId);
			warChapter.setRoleId(role.getRoleId());
			// 插入数据库
			warChapterDao.insert(warChapter);

			warChapterMap.put(chapterId, warChapter);
		}

		Map<Integer, WarBuild> warBuildMap = role.getWar().getWarBuildMap();
		// 如果没有这个建筑则加入
		WarBuild warBuild = warBuildMap.get(buildId);
		if (warBuild == null) {
			warBuild = new WarBuild();
			warBuild.setBuildId(buildId);
			warBuild.setRoleId(role.getRoleId());
			warBuild.setStarCount(0);
			// 插入数据库
			warBuildDao.insert(warBuild);

			warBuildMap.put(buildId, warBuild);
		}

		final int npcTeam = this.getNPCTeam(buildId);
		FightEventListener listener = new FightEventListenerAdapter(role) {
			@Override
			public int getAI() {
				return npcTeam;
			}

			@Override
			public GameFightType getReturnType(Role role) {
				return GameFightType.WAR;
			}
		};
		session.write(SCMessage.newBuilder()
				.setWarMarchResponse(WarMarchResponse.newBuilder()).build());

		role.getWar().setMarchBuildId(buildId);
		matchService.matchRole(session, role, true, listener);
	}

	private boolean checkBuild(Role role, int chapterId, int buildId) {
		// 检查前一章节
		if (!this.checkChapter(role, chapterId)) {
			return false;
		}

		// 检查前一建筑
		List<Integer> buildIdList = this.getWarBuildByChapterId(chapterId);
		// 获得该建筑的索引
		int index = buildIdList.indexOf(buildId);
		// 如果是第一个值，则给予通过，否则就要检查前面一个建筑是否获得至少一星
		if (index > 0) {
			int formerIndex = index - 1;
			int formerBuildId = buildIdList.get(formerIndex);
			WarBuild formerWarBuild = role.getWar().getWarBuildMap().get(formerBuildId);
			if (formerWarBuild == null || formerWarBuild.getStarCount() == 0) {
				return false;
			}
		}

		return true;
	}

	private boolean checkChapter(Role role, int chapterId) {
		List<Integer> chapterIdList = this.getChapterId();
		int index = chapterIdList.indexOf(chapterId);
		// 如果是第一章则通过
		if (index == 0) {
			return true;
		}
		int formerId = chapterIdList.get(index - 1);
		WarChapter warChapter = role.getWar().getWarChapterMap().get(formerId);
		// 如果没有前一章数据或前一章没有打完则返回false
		// 如果没有前一章数据
		if (warChapter == null) {
			return false;
		}

		// 检查前一章最后一个建筑是否获得至少一星
		List<Integer> warBuildIdList = getWarBuildByChapterId(formerId);
		int warBuildId = warBuildIdList.get(warBuildIdList.size() - 1);
		WarBuild warBuild = role.getWar().getWarBuildMap().get(warBuildId);
		if (warBuild == null || warBuild.getStarCount() == 0) {
			return false;
		}

		return true;
	}

	@Override
	public void refreshWarBuild(Role role, GameResultData gameResultData,
			GameResultAwardData.Builder gameResultAwardDataBuilder) {
		// 如果没有赢，则返回
		if (gameResultData.getGameResult() != GameResult.WIN)
			return;

		War war = role.getWar();

		// 获得行军的建筑
		int buildId = war.getMarchBuildId();
		if (buildId == 0) {
			// error
			return;
		}
		// 如果赢了，则获得比赛结果中的星数
		int starCount = gameResultData.getStarCount();
		WarBuild warBuild = war.getWarBuildMap().get(buildId);
		// 如果是第一次胜利，则奖励
		int money = 0;
		if (warBuild.getStarCount() == 0 && starCount != 0) {
			money = this.getWarBuildWinMoney(buildId);
			roleService.addMoney(role, money);
		}

		// 更新星数，只有超过才会更新
		if (warBuild.getStarCount() < starCount) {
			warBuild.setStarCount(starCount);
		}
		gameResultAwardDataBuilder.setMoney(money);
	}

	@Override
	public GeneratedMessage getChapterAward(Role role, int chapterId) {
		War war = role.getWar();
		WarChapter warChapter = war.getWarChapterMap().get(chapterId);

		if (warChapter.getAward() == WarConstant.CHPATER_AWARD_GET) {
			return SCMessage
					.newBuilder()
					.setWarGetChapterAwardResponse(
							WarGetChapterAwardResponse.newBuilder().setErrorCode(ErrorCode.REPEAT_GET_AWARD.getNumber())).build();
		}

		// 是否可以领取章节奖励
		if (this.canGetChapterAward(role, chapterId) == WarConstant.CHPATER_AWARD_REJECT) {
			return SCMessage
					.newBuilder()
					.setWarGetChapterAwardResponse(
							WarGetChapterAwardResponse.newBuilder()
									.setErrorCode(ErrorCode.CHAPTER_AWARD_NEED_STAR_FULL.getNumber())).build();
		}
		warChapter.setAward(WarConstant.CHPATER_AWARD_GET);

		// 章节银币奖励
		int money = this.getChapterWinMoney(chapterId);
		roleService.addMoney(role, money);

		// 章节卡牌奖励
		List<Card> chapterAwardCardList = this.getChapterWinCard(chapterId);

		for (Card chapterAwardCard : chapterAwardCardList) {
			if (chapterAwardCard.getCardId() != 0) {
				int cardId = chapterAwardCard.getCardId();
				int cardNum = chapterAwardCard.getNum();

				Card card = role.getCardMap().get(cardId);
				if (card == null) {
					card = cardService.getCard(role, cardId);
					role.getCardMap().put(card.getCardId(), card);
				}
				card.setNum(card.getNum() + cardNum);

				// 商城刷新
				marketItemRefresh(role, cardId);
			}
		}

		return SCMessage.newBuilder().setWarGetChapterAwardResponse(WarGetChapterAwardResponse.newBuilder()).build();
	}

	/**
	 * 获取章节奖励信息
	 * 
	 * @param role
	 * @param chapterId
	 * @return
	 */
	private int canGetChapterAward(Role role, int chapterId) {
		WarChapter warChapter = role.getWar().getWarChapterMap().get(chapterId);

		// 没有章节，所以不可以领取
		if (warChapter == null) {
			return WarConstant.CHPATER_AWARD_REJECT;
		}

		// 如果已经领取过了或者可以领取的直接返回
		if (warChapter.getAward() == WarConstant.CHPATER_AWARD_GET
				|| warChapter.getAward() == WarConstant.CHPATER_AWARD_ALLOW) {
			return warChapter.getAward();
		}

		// 检查是否可以领取
		List<Integer> warBuildIdList = this.getWarBuildByChapterId(chapterId);
		for (Integer buildId : warBuildIdList) {
			WarBuild warBuild = role.getWar().getWarBuildMap().get(buildId);
			// 有建筑是空或者没有满星,则不能领取
			if (warBuild == null || warBuild.getStarCount() != this.getWarBuildFullStar(buildId)) {
				return WarConstant.CHPATER_AWARD_REJECT;
			}
		}
		// 可以领取则进行标记赋值
		warChapter.setAward(WarConstant.CHPATER_AWARD_ALLOW);
		return WarConstant.CHPATER_AWARD_ALLOW;
	}

	private ChapterAwardState getChapterAwardState(int state) {
		ChapterAwardState e = ChapterAwardState.REJECT;
		if (state == WarConstant.CHPATER_AWARD_ALLOW) {
			e = ChapterAwardState.ALLOW;
		} else if (state == WarConstant.CHPATER_AWARD_GET) {
			e = ChapterAwardState.GET;
		} else if (state == WarConstant.CHPATER_AWARD_REJECT) {
			e = ChapterAwardState.REJECT;
		}
		return e;
	}

	/**
	 * 检查领取的卡牌商城中是否要用人民币买的同一张，如果有则变成用银币买
	 * 
	 * @param role
	 * @param cardId
	 */
	private void marketItemRefresh(Role role, int cardId) {
		Market market = role.getMarket();
		Map<Integer, MarketItem> marketItemMap = market.getMarketItemMap();

		for (MarketItem marketItem : marketItemMap.values()) {
			if (marketItem.getId() == cardId && marketItem.getBuyType() == MarketBuyType.GOLD) {
				marketItem.setDayBuyCount(0);
				marketItem.setBuyType(MarketBuyType.MONEY);
			}
		}
	}

	/**
	 * 根据章节获得建筑id列表
	 * 
	 * @param chapterId
	 * @return
	 * @author wcy 2017年1月10日
	 */
	private List<Integer> getWarBuildByChapterId(int chapterId) {
		return WarBuildConfigCache.getBuildIdListByChapterId(chapterId);
	}

	private List<Integer> getChapterId() {
		return WarChapterConfigCache.getChapterIdList();
	}

	private int getChapterIdByBuildId(int buildId) {
		return WarBuildConfigCache.getWarBuildConfigByBuildId(buildId).chapterId;
	}

	private int getNPCTeam(int buildId) {
		return WarBuildConfigCache.getWarBuildConfigByBuildId(buildId).npcTeam;
	}

	private int getWarBuildWinMoney(int buildId) {
		return WarBuildConfigCache.getWarBuildConfigByBuildId(buildId).award;
	}

	private int getChapterWinMoney(int chapterId) {
		return WarChapterConfigCache.getChapterById(chapterId).moneyAward;
	}

	private List<Card> getChapterWinCard(int chapterId) {
		return ChapterCardFormatter.formatCard(WarChapterConfigCache.getChapterById(chapterId).cardAward);
	}

	private int getWarBuildFullStar(int buildId) {
		return 3;
	}
}
