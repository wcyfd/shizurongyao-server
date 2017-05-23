package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.List;

import com.randioo.randioo_server_base.utils.StringJoiner;

public class CardList {
	private int index;
	/** 卡组ID MAX:11 */
	private List<Integer> list = new ArrayList<Integer>();
	/** 主将ID */
	private int mainId;

	public CardList() {
		// TODO Auto-generated constructor stub
	}

	public CardList(String value) {
		String[] tempStr = value.split(",");
		this.index = Integer.parseInt(tempStr[0]);
		this.mainId = Integer.parseInt(tempStr[1]);
		for (int i = 2; i < tempStr.length; i++) {
			if (!tempStr[i].equals("")) {
				this.list.add(Integer.parseInt(tempStr[i]));
			}
		}
	}

	public List<Integer> getList() {
		return list;
	}

	public void setList(List<Integer> list) {
		this.list = list;
	}

	public int getMainId() {
		return mainId;
	}

	public void setMainId(int mainId) {
		this.mainId = mainId;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(this.mainId).append(",");
		int count = 0;
		for (Integer x : this.list) {
			count++;
			if (count >= this.list.size()) {
				sb.append(x).append(";");
			} else {
				sb.append(x).append(",");
			}
		}

		return sb.toString();
	}
}
