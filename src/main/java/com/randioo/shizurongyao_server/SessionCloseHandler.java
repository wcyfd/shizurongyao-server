package com.randioo.shizurongyao_server;

import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.net.SpringContext;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.shizurongyao_server.cache.local.ClanCache;
import com.randioo.shizurongyao_server.dao.CardDao;
import com.randioo.shizurongyao_server.dao.ClanMemberDao;
import com.randioo.shizurongyao_server.dao.RoleDao;
import com.randioo.shizurongyao_server.dao.WarBuildDao;
import com.randioo.shizurongyao_server.dao.WarChapterDao;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.bo.WarBuild;
import com.randioo.shizurongyao_server.entity.bo.WarChapter;
import com.randioo.shizurongyao_server.module.fight.service.FightService;
import com.randioo.shizurongyao_server.module.match.service.MatchService;
import com.randioo.shizurongyao_server.protocol.Entity.GameResult;

/**
 * session关闭角色数据处理
 * 
 */
public class SessionCloseHandler {
	/**
	 * 移除session缓存
	 * 
	 * @param id
	 */
	public static void asynManipulate(Role role) {

		System.out.println("[account:" + role.getAccount() + ",name:" + role.getName() + "] manipulate");

		SessionCache.removeSessionById(role.getRoleId());

		FightService fightService = SpringContext.getBean("fightService");
		MatchService matchService = SpringContext.getBean("matchService");

		matchService.offline(role);
		fightService.offline(role, GameResult.LOSS);
		role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());

		GameDB gameDB = SpringContext.getBean("gameDB");
		if (!gameDB.isUpdatePoolClose()) {
			gameDB.getUpdatePool().submit(new DBRunnable<Role>(role) {
				@Override
				public void run(Role role) {
					roleDataCache2DB(role, true);
				}
			});
		}
	}

	public static void roleDataCache2DB(Role role, boolean mustSave) {
		try {
			CardDao cardDao = (CardDao) SpringContext.getBean("cardDao");
			for (Card card : role.getCardMap().values()) {
				if (SaveUtils.needSave(card, mustSave)) {
					cardDao.update(card);
				}
			}

			RoleDao roleDao = (RoleDao) SpringContext.getBean("roleDao");
			if (SaveUtils.needSave(role, mustSave)) {
				roleDao.update(role);
			}

			WarBuildDao warBuildDao = (WarBuildDao) SpringContext.getBean("warBuildDao");
			for (WarBuild warBuild : role.getWar().getWarBuildMap().values()) {
				if (SaveUtils.needSave(warBuild, mustSave))
					warBuildDao.update(warBuild);
			}

			WarChapterDao warChapterDao = (WarChapterDao) SpringContext.getBean("warChapterDao");
			for (WarChapter warChapter : role.getWar().getWarChapterMap().values()) {
				if (SaveUtils.needSave(warChapter, mustSave))
					warChapterDao.update(warChapter);
			}

			ClanMemberDao clanMemberDao = (ClanMemberDao) SpringContext.getBean("clanMemberDao");
			for (ClanMember clanMember : ClanCache.getClanMemberMap().values()) {
				if (SaveUtils.needSave(clanMember, mustSave))
					clanMemberDao.update(clanMember);
			}

		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("id:" + role.getRoleId() + ",account:" + role.getAccount() + ",name:" + role.getName()
					+ "] save error");
		}

	}

}
