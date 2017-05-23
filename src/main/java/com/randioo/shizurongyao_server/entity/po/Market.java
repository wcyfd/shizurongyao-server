package com.randioo.shizurongyao_server.entity.po;

import java.util.HashMap;
import java.util.Map;

import com.randioo.randioo_server_base.utils.db.Saveable;

/**
 * 商城
 * 
 * @author wcy 2016年6月7日
 *
 */
public class Market {
	/** 玩家id */
	private int roleId;
	/** 刷新时间 */
	private String refreshTime;
	/** 刷新次数 */
	private int refreshCount;
	/** 刷新物品表 */
	private Map<Integer, MarketItem> marketItemMap = new HashMap<>();
	/** 是否显示动画 */
	private boolean showAnimation;

	public int getRoleId() {
		return roleId;
	}

	public void setRoleId(int roleId) {
		this.roleId = roleId;
	}

	public String getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(String refreshTime) {
		this.refreshTime = refreshTime;
	}

	public Map<Integer, MarketItem> getMarketItemMap() {
		return marketItemMap;
	}

	public int getRefreshCount() {
		return refreshCount;
	}

	public void setRefreshCount(int refreshCount) {
		this.refreshCount = refreshCount;
	}

	public boolean isShowAnimation() {
		return showAnimation;
	}

	public void setShowAnimation(boolean showAnimation) {
		this.showAnimation = showAnimation;
	}

}
