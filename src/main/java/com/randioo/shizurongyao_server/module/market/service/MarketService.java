package com.randioo.shizurongyao_server.module.market.service;

import org.apache.mina.core.session.IoSession;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;

/**
 * 
 * @author AIM
 *
 */
public interface MarketService extends ObserveBaseServiceInterface {
	/**
	 * 商城初始化使用
	 * 
	 * @param role
	 */
	public void marketInit(Role role);

	/**
	 * 刷新商城信息
	 * 
	 * @param role
	 * @param nowTime 当前时间
	 * @return 是否刷新成功
	 * @author wcy 2017年1月9日
	 */
	void refreshMarketItem(Role role, String nowTime);

	/**
	 * 人工刷新
	 * @param role
	 * @return
	 * @author wcy 2017年1月9日
	 */
	GeneratedMessage artificalRefreshMarketItem(Role role);
	/**
	 * 显示商品
	 * 
	 * @param role
	 * @return
	 */
	GeneratedMessage showMarketItem(Role role);

	/**
	 * 购买商品
	 * 
	 * @param role
	 * @param id
	 * @return
	 */
	GeneratedMessage buyMarketItem(Role role, IoSession session, int index);

	void newRoleInit(Role role);

}
