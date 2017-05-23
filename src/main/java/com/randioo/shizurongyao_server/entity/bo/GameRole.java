package com.randioo.shizurongyao_server.entity.bo;

import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.match.MatchInfo;
import com.randioo.randioo_server_base.module.match.Matchable;
import com.randioo.randioo_server_base.utils.db.DataEntity;

public abstract class GameRole extends DataEntity implements RoleInterface, Matchable {
	protected MatchInfo matchInfo;
	protected boolean isNPC;

	@Override
	public void setMatchInfo(MatchInfo matchInfo) {
		this.matchInfo = matchInfo;
	}

	@Override
	public MatchInfo getMatchInfo() {
		return matchInfo;
	}

	@Override
	public void setNpc(boolean isNpc) {
		this.isNPC = isNpc;
	}

	@Override
	public boolean isNPC() {
		return isNPC;
	}

	protected String account;
	protected String name;
	protected int roleId;
	/** 登录时间 */
	private String loginTimeStr;
	/** 离线时间 */
	private String offlineTimeStr;
	/** 帐号创建时间 */
	private String createTimeStr;
	/** 载入时间 */
	private String loadTimeStr;

	@Override
	public String getAccount() {
		return account;
	}

	@Override
	public void setAccount(String account) {
		this.account = account;
		setChange(true);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
		setChange(true);
	}

	@Override
	public int getRoleId() {
		return roleId;
	}

	@Override
	public void setRoleId(int roleId) {
		this.roleId = roleId;
		setChange(true);
	}

	@Override
	public String getOfflineTimeStr() {
		return offlineTimeStr;
	}

	@Override
	public void setOfflineTimeStr(String offlineTimeStr) {
		setChange(true);
		this.offlineTimeStr = offlineTimeStr;
	}

	@Override
	public String getLoginTimeStr() {
		return loginTimeStr;
	}

	@Override
	public void setLoginTimeStr(String loginTimeStr) {
		setChange(true);
		this.loginTimeStr = loginTimeStr;
	}

	@Override
	public String getCreateTimeStr() {
		return createTimeStr;
	}

	@Override
	public void setCreateTimeStr(String createTimeStr) {
		setChange(true);
		this.createTimeStr = createTimeStr;
	}

	@Override
	public void setLoadTimeStr(String loadTimeStr) {
		setChange(true);
		this.loadTimeStr = loadTimeStr;
	}

	@Override
	public String getLoadTimeStr() {
		return loadTimeStr;
	}

}
