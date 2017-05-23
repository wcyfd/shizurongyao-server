package com.randioo.shizurongyao_server.entity.bo;

import com.randioo.randioo_server_base.utils.db.DataEntity;

public class Card extends DataEntity {
	/** role ID */
	private int roleId;
	/** card ID */
	private int cardId;
	/** 等级 */
	private int lv;
	/** 数量 */
	private int num;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
		setChange(true);
	}

	public int getCardId() {
		return cardId;
	}

	public void setCardId(int cardId) {
		this.cardId = cardId;
		setChange(true);
	}

	public int getLv() {
		return lv;
	}

	public void setLv(int lv) {
		this.lv = lv;
		setChange(true);
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
		setChange(true);
	}

}
