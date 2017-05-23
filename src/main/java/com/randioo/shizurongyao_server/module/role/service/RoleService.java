package com.randioo.shizurongyao_server.module.role.service;

import java.util.List;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.protocol.Entity.RoleInfoType;

public interface RoleService extends ObserveBaseServiceInterface {

	/**
	 * 增加玩家银币
	 * 
	 * @param role
	 * @param value
	 */

	public void addMoney(Role role, int value);

	// /**
	// * 增加玩家粮食
	// *
	// * @param role
	// * @param value
	// */
	// public void addFood(Role role, int value);

	/**
	 * 增加玩家金币
	 * 
	 * @param role
	 * @param gold
	 * @author wcy 2016年6月2日
	 */
	public void addGold(Role role, int value);

	/**
	 * 获得玩家
	 * 
	 * @param roleId
	 * @return
	 * @author wcy 2016年5月24日
	 */
	// public Role getRoleById(int roleId);
	//
	// /**
	// * 获得玩家信息
	// *
	// * @param role
	// * @param roleId
	// * @return
	// */
	// Message getRoleInfo(int roleId);
	//
	// /**
	// * 检查名称是否合法
	// *
	// * @param name
	// * @return 0合法 1过长 2敏感字
	// * @author wcy 2016年7月15日
	// */
	// byte checkNameIllege(String name);
	//
	// /**
	// * 重命名
	// *
	// * @param role
	// * @param newName
	// * @return
	// * @author wcy 2016年7月15日
	// */
	// Message rename(Role role, String newName);
	//
	// /**
	// * 改变是否接受消息
	// *
	// * @param role
	// * @param receive
	// * @return
	// * @author wcy 2016年7月15日
	// */
	// Message changeReceiveMessage(Role role, byte receive);
	//
	// /**
	// * 显示重命名花费
	// *
	// * @param role
	// * @return
	// * @author wcy 2016年7月15日
	// */
	// Message showRenameInfo(Role role);

	// Role getRoleByAccount(String account);
	//
	// /**
	// *
	// * @param role
	// * @param value
	// * @param payMethod <br>
	// * 0&nbsp;表示正常途径获得的资源<br>
	// * 1&nbsp;表示元宝途径获得的资源<br>
	// * 2&nbsp;表示GM工具途径获得的资源
	// * @author wcy 2016年9月6日
	// */
	// void addFood(Role role, int value, byte payMethod);

	/**
	 * 
	 * @param role
	 * @param value
	 * @param payMethod <br>
	 *            0&nbsp;表示正常途径获得的资源<br>
	 *            1&nbsp;表示元宝途径获得的资源<br>
	 *            2&nbsp;表示GM工具途径获得的资源
	 * @author wcy 2016年9月6日
	 */
	void addMoney(Role role, int value, byte payMethod);

	GeneratedMessage selectRoleInfo(Role role, List<RoleInfoType> roleInfoType);

	void addPoint(Role role, int addValue);

	void newRoleInit(Role role);

	public void roleInit(Role role);

	GeneratedMessage rename(Role role, String name, int clanId);

}
