package com.randioo.shizurongyao_server.entity.po;

public class Resource {
	/** 资源类型 */
	private byte resType;
	/** 资源id */
	private int itemId;
	/** 资源数量 */
	private int num;

	public byte getResType() {
		return resType;
	}

	public void setResType(byte resType) {
		this.resType = resType;
	}

	public int getItemId() {
		return itemId;
	}

	public void setItemId(int itemId) {
		this.itemId = itemId;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(resType).append(",").append(itemId).append(",").append(num).append(";");
		return sb.toString();

	}
}
