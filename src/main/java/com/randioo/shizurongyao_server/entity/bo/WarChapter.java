package com.randioo.shizurongyao_server.entity.bo;

import com.randioo.randioo_server_base.utils.db.DataEntity;

public class WarChapter extends DataEntity {
	/** 玩家id */
	private int roleId;
	/** 章节id */
	private int chapterId;
	/** 数量 */
	private int award;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getChapterId() {
		return chapterId;
	}

	public void setChapterId(int chapterId) {
		this.chapterId = chapterId;
	}

	public int getAward() {
		return award;
	}

	public void setAward(int award) {
		setChange(true);
		this.award = award;
	}

}
