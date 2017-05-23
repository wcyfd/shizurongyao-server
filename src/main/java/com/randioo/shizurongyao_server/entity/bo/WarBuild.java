package com.randioo.shizurongyao_server.entity.bo;

import com.randioo.randioo_server_base.utils.db.DataEntity;

public class WarBuild extends DataEntity {
	/** 玩家id */
	private int roleId;
	/** 建筑id */
	private int buildId;
	/** 星数 */
	private int starCount;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getBuildId() {
		return buildId;
	}

	public void setBuildId(int buildId) {
		this.buildId = buildId;
	}

	public int getStarCount() {
		return starCount;
	}

	public void setStarCount(int starCount) {
		setChange(true);
		this.starCount = starCount;
	}

}
