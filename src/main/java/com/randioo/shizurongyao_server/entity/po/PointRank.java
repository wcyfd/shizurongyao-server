package com.randioo.shizurongyao_server.entity.po;

import com.randioo.randioo_server_base.utils.db.DataEntity;

public class PointRank extends DataEntity {
	/** 角色 id */
	private int roleId;
	/** 角色 Name */
	private String roleName;
	/** 积分 */
	private int point;
	/** 出战卡牌 */
	private String listStr;
	/** 正在使用的卡组id */
	private int useCardsId;
	/** 胜利次数 */
	private int winCount;

	public int getUseCardsId() {
		return useCardsId;
	}

	public void setUseCardsId(int useCardsId) {
		this.useCardsId = useCardsId;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public int getPoint() {
		return point;
	}

	public void setPoint(int point) {
		this.point = point;
	}

	public String getListStr() {
		return listStr;
	}

	public void setListStr(String listStr) {
		this.listStr = listStr;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public int getWinCount() {
		return winCount;
	}

	public void setWinCount(int winCount) {
		this.winCount = winCount;
	}

}
