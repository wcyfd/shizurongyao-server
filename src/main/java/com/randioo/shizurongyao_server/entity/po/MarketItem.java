package com.randioo.shizurongyao_server.entity.po;

import com.randioo.shizurongyao_server.module.market.MarketConstant.MarketBuyType;

/**
 * 商品
 * 
 * @author wcy 2017年1月9日
 *
 */
public class MarketItem {
	/** 商品id */
	private int id;
	/** 已经购买的次数 */
	private int dayBuyCount;
	/** 购买方式 */
	private MarketBuyType buyType;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDayBuyCount() {
		return dayBuyCount;
	}

	public void setDayBuyCount(int dayBuyCount) {
		this.dayBuyCount = dayBuyCount;
	}

	public MarketBuyType getBuyType() {
		return buyType;
	}

	public void setBuyType(MarketBuyType buyType) {
		this.buyType = buyType;
	}

}
