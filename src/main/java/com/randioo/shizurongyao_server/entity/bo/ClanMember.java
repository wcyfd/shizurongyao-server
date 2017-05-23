package com.randioo.shizurongyao_server.entity.bo;

import com.randioo.randioo_server_base.utils.db.DataEntity;

public class ClanMember extends DataEntity {
	/** 部落id */
	private int clanId;
	/** 玩家id */
	private int roleId;

	public int getClanId() {
		return clanId;
	}

	public void setClanId(int clanId) {
		setChange(true);
		this.clanId = clanId;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		setChange(true);
		this.roleId = roleId;
	}

}
