package com.randioo.shizurongyao_server.entity.po;

public class FightEvent {
	private int x;
	private int y;
	private int monsterId;
	private String account;
	private String uuid;
	private int step;
	private byte side;
	private int monsterLv;
	private int roleId;

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getMonsterId() {
		return monsterId;
	}

	public void setMonsterId(int id) {
		this.monsterId = id;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public int getStep() {
		return step;
	}

	public void setStep(int step) {
		this.step = step;
	}

	public byte getSide() {
		return side;
	}

	public void setSide(byte side) {
		this.side = side;
	}

	public int getMonsterLv() {
		return monsterLv;
	}

	public void setMonsterLv(int monsterLv) {
		this.monsterLv = monsterLv;
	}

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

}
