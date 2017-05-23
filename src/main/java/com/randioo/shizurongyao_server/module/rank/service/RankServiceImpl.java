package com.randioo.shizurongyao_server.module.rank.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.Lock;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.lock.RWCacheLockUtil;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.file.FamilyNameConfigCache;
import com.randioo.shizurongyao_server.cache.local.RankCache;
import com.randioo.shizurongyao_server.dao.PointRankDao;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.file.FamilyNameConfig;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.entity.po.PointRank;
import com.randioo.shizurongyao_server.module.rank.PointRankConstant;
import com.randioo.shizurongyao_server.protocol.Entity.CardListData;
import com.randioo.shizurongyao_server.protocol.Entity.PointRankData;
import com.randioo.shizurongyao_server.protocol.Rank.RankShowClanPointRankResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;

@Service("rankService")
public class RankServiceImpl extends ObserveBaseService implements RankService {

	@Autowired
	private PointRankDao pointRankDao;

	@Override
	public void initService() {
		this.refreshPointRank();
	}

	@Override
	public void refreshPointRank() {
		Map<Integer, FamilyNameConfig> map = FamilyNameConfigCache.getFamilyNameMap();
		for (int clanId : map.keySet()) {
			Lock lock = RWCacheLockUtil.getWriteLock(RankCache.class, clanId);
			try {
				lock.lock();
				List<PointRank> list = RankCache.getPointRankList(clanId);
				if (list == null) {
					list = new ArrayList<>();
					RankCache.pointRankMap().put(clanId, list);
				}
				list.clear();

				List<PointRank> pointRankList = pointRankDao.selectPointRank(clanId, PointRankConstant.RANT_COUNT);
				list.addAll(pointRankList);
			} finally {
				lock.unlock();
			}
		}
	}

	@Override
	public GeneratedMessage showRank(Role role, int clanId) {
		Lock lock = RWCacheLockUtil.getReadLock(RankCache.class, clanId);
		try {
			lock.lock();
			List<PointRank> pointRankList = RankCache.getPointRankList(clanId);

			RankShowClanPointRankResponse.Builder builder = RankShowClanPointRankResponse.newBuilder();
			int rank = 1;
			for (PointRank pointRank : pointRankList) {
				Map<Integer, CardList> cardListMap = new HashMap<>();
				String listStr = pointRank.getListStr();
				String str[] = listStr.split(";");
				for (String x : str) {
					CardList temp = new CardList(x);
					cardListMap.put(temp.getIndex(), temp);
				}
				CardList cardList = cardListMap.get(pointRank.getUseCardsId());

				builder.addPointRankData(PointRankData
						.newBuilder()
						.setCardListData(
								CardListData.newBuilder().setMainId(cardList.getMainId())
										.setCardListIndex(cardList.getIndex()).addAllCardIds(cardList.getList()))
						.setName(pointRank.getRoleName()).setPoint(pointRank.getPoint())
						.setWinCount(pointRank.getWinCount()).setRank(rank));
				rank++;
			}

			return SCMessage.newBuilder().setRankShowClanPointRankResponse(builder).build();
		} finally {
			lock.unlock();
		}

	}
}
