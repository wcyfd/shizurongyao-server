package com.randioo.shizurongyao_server.module.clan.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.shizurongyao_server.cache.file.FamilyNameConfigCache;
import com.randioo.shizurongyao_server.cache.local.ClanCache;
import com.randioo.shizurongyao_server.dao.ClanMemberDao;
import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.Clan;
import com.randioo.shizurongyao_server.module.clan.ClanConstant;
import com.randioo.shizurongyao_server.module.rank.service.RankService;
import com.randioo.shizurongyao_server.protocol.Clan.ClanShowAllClanResponse;
import com.randioo.shizurongyao_server.protocol.Entity.ClanData;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;

@Service("clanService")
public class ClanServiceImpl extends ObserveBaseService implements ClanService {
	@Autowired
	private ClanMemberDao clanMemberDao;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private RankService rankService;

	@Override
	public void init() {
		// 加载所有的部落
		List<Integer> clanIdList = new ArrayList<>(FamilyNameConfigCache.getFamilyNameMap().keySet());
		for (int clanId : clanIdList) {
			Clan clan = new Clan();
			clan.setClanId(clanId);

			ClanCache.getClanMap().put(clan.getClanId(), clan);

			List<ClanMember> list = clanMemberDao.selectList(clanId);
			for (ClanMember clanMember : list) {
				clan.getMemberMap().put(clanMember.getRoleId(), clanMember);
			}
		}
	}

	@Override
	public ClanMember getClanMember(int roleId, int clanId) {
		ClanMember clanMember = ClanCache.getClanMemberMap().get(roleId);
		if (clanMember == null) {
			clanMember = new ClanMember();
			clanMember.setRoleId(roleId);
			clanMember.setClanId(clanId);

			ClanCache.getClanMemberMap().put(clanMember.getRoleId(), clanMember);

			gameDB.getInsertPool().submit(new DBRunnable<ClanMember>(clanMember) {

				@Override
				public void run(ClanMember entity) {
					clanMemberDao.insert(entity);
				}
			});
		}

		return clanMember;
	}

	@Override
	public void joinClan(Role role, int clanId) {
		Clan clan = ClanCache.getClanMap().get(clanId);
		ClanMember clanMember = this.getClanMember(role.getRoleId(), clanId);

		clan.getMemberMap().put(role.getRoleId(), clanMember);

		notifyObservers(ClanConstant.JOIN_CLAIN, role, clanId);
	}

	@Override
	public GeneratedMessage showClan(Role role) {
		Map<Integer, Clan> clanMap = ClanCache.getClanMap();
		ClanShowAllClanResponse.Builder builder = ClanShowAllClanResponse.newBuilder();
		for (Clan clan : clanMap.values()) {
			ClanData clanData = parseClan2ClanData(clan);
			builder.addClanData(clanData);
		}

		return SCMessage.newBuilder().setClanShowAllClanResponse(builder).build();
	}

	private ClanData parseClan2ClanData(Clan clan) {
		int size = clan.getMemberMap().size();
		return ClanData.newBuilder().setClanId(clan.getClanId()).setClanCount(size).build();
	}

	// public static void main(String[] args) {
	// // Map<ClanMember,ClanMember> map = new ConcurrentSkipListMap<>(new
	// // Comparator<ClanMember>() {
	// //
	// // @Override
	// // public int compare(ClanMember o1, ClanMember o2) {
	// // // TODO Auto-generated method stub
	// // return o1.getState()-o2.getState();
	// // }
	// // });
	//
	// List<ClanMember> list = new ArrayList<>();
	// ClanMember c1 = new ClanMember();
	// ClanMember c2 = new ClanMember();
	// c1.setRoleId(1);
	// c2.setRoleId(2);
	// c1.setState(11);
	// c2.setState(4);
	//
	// list.add(c1);
	// list.add(c2);
	//
	// Collections.sort(list, new Comparator<ClanMember>() {
	//
	// @Override
	// public int compare(ClanMember o1, ClanMember o2) {
	// // TODO Auto-generated method stub
	// return o1.getState() - o2.getState();
	// }
	// });
	//
	// for (ClanMember clanMember : list) {
	// System.out.println(clanMember.getRoleId() + " " + clanMember.getState());
	// }
	// }

}
