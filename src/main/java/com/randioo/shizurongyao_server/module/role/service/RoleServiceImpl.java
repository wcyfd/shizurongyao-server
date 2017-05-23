package com.randioo.shizurongyao_server.module.role.service;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.module.role.RoleHandler;
import com.randioo.randioo_server_base.module.role.RoleModelService;
import com.randioo.randioo_server_base.utils.GameUtils;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.game.IdClassCreator;
import com.randioo.randioo_server_base.utils.sensitive.SensitiveWordDictionary;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.template.Ref;
import com.randioo.shizurongyao_server.cache.file.WarChapterConfigCache;
import com.randioo.shizurongyao_server.dao.RoleDao;
import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.module.clan.service.ClanService;
import com.randioo.shizurongyao_server.module.login.LoginConstant;
import com.randioo.shizurongyao_server.protocol.Entity.RoleInfoType;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.Role.RoleInfoSelectResponse;
import com.randioo.shizurongyao_server.protocol.Role.RoleRenameResponse;
import com.randioo.shizurongyao_server.protocol.Role.SCRoleGold;
import com.randioo.shizurongyao_server.protocol.Role.SCRoleMoney;
import com.randioo.shizurongyao_server.protocol.Role.SCRolePoint;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;
import com.randioo.shizurongyao_server.util.SessionUtils;

@Service("roleService")
public class RoleServiceImpl extends ObserveBaseService implements RoleService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private GameDB gameDB;

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private RoleModelService roleModelService;

	@Autowired
	private ClanService clanService;

	@Override
	public void init() {
		Integer maxRoleId = roleDao.getMaxRoleId();
		idClassCreator.initId(Role.class, maxRoleId == null ? 0 : maxRoleId);
		roleModelService.setRoleHandler(new RoleHandler() {

			Pattern p = Pattern.compile("^[a-zA-Z\u4e00-\u9fa5]+$");

			@Override
			public boolean checkNewNameIllege(String name, Ref<Integer> errorCode) {
				// TODO Auto-generated method stub

				if (name.length() >= 10) {
					errorCode.set(LoginConstant.CREATE_ROLE_NAME_TOO_LONG);
					return false;
				}

				if (SensitiveWordDictionary.containsSensitiveWord(name)) {
					errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
					return false;
				}

				if (RoleCache.getNameSet().containsKey(name)) {
					errorCode.set(LoginConstant.CREATE_ROLE_NAME_REPEATED);
					return false;
				}

				// 检查特殊字符
				if (!p.matcher(name).find()) {
					errorCode.set(LoginConstant.CREATE_ROLE_NAME_CHAR);
					return false;
				}

				return true;

			}
		});
	}

	@Override
	public void newRoleInit(Role role) {
		// 设置战场的第一章
		role.setRoleId(idClassCreator.getId(Role.class));
		role.setName("用户" + role.getRoleId());
		role.setCurrentChapterId(WarChapterConfigCache.getMinChapterId());
	}

	@Override
	public void roleInit(Role role) {
		// TODO Auto-generated method stub
	}

	@Override
	public void addMoney(Role role, int value) {
		byte method = 0;
		this.addMoney(role, value, method);

	}

	@Override
	public void addMoney(Role role, int value, byte method) {
		int originTotal = role.getMoney();
		int total = GameUtils.protectedGetTotalValue(originTotal, value);

		if (method == 0) {// 正常
			// 如果还没有到达容量上限，则加入
			role.setMoney(total);
			value = total - originTotal;
		} else if (method == 1) {// 金币
			role.setMoney(total);
			value = total - originTotal;
		} else if (method == 2) {// GM
			role.setMoney(total);
			value = total - originTotal;
		}
		IoSession ioSession = SessionCache.getSessionById(role.getRoleId());

		if (ioSession != null) {
			ioSession.write(SCMessage.newBuilder().setSCRoleMoney(SCRoleMoney.newBuilder().setMoney(total)).build());
		}
	}

	@Override
	public void addGold(Role role, int value) {
		int total = GameUtils.protectedGetTotalValue(role.getGold(), value);
		role.setGold(total);

		SessionUtils.sc(role, SCMessage.newBuilder().setSCRoleGold(SCRoleGold.newBuilder().setGold(total)).build());
	}

	@Override
	public GeneratedMessage selectRoleInfo(Role role, List<RoleInfoType> roleInfoType) {
		RoleInfoSelectResponse.Builder response = RoleInfoSelectResponse.newBuilder().addAllRoleInfoType(roleInfoType);
		for (RoleInfoType type : roleInfoType) {
			switch (type) {
			case INFO_MONEY:
				response.setMoney(role.getMoney());
			}

		}
		return SCMessage.newBuilder().setRoleInfoSelectResponse(response).build();
	}

	@Override
	public void addPoint(Role role, int addValue) {
		role.setPoint(GameUtils.protectedGetTotalValue(role.getPoint(), addValue));
		IoSession ioSession = SessionCache.getSessionById(role.getRoleId());
		if (ioSession != null) {
			ioSession.write(SCMessage.newBuilder().setSCRolePoint(SCRolePoint.newBuilder().setPoint(role.getPoint()))
					.build());
		}
	}

	@Override
	public GeneratedMessage rename(Role role, String name, int clanId) {
		Ref<Integer> errorCode = new Ref<>();
		boolean success = roleModelService.rename(role, name, errorCode);
		if (!success) {
			ErrorCode errorCodeEnum = null;
			switch (errorCode.get()) {
			case LoginConstant.CREATE_ROLE_NAME_SENSITIVE:
				errorCodeEnum = ErrorCode.NAME_SENSITIVE;
				break;
			case LoginConstant.CREATE_ROLE_NAME_REPEATED:
				errorCodeEnum = ErrorCode.NAME_REPEATED;
				break;
			case LoginConstant.CREATE_ROLE_NAME_TOO_LONG:
				errorCodeEnum = ErrorCode.NAME_TOO_LONG;
				break;
			case LoginConstant.CREATE_ROLE_NAME_CHAR:
				errorCodeEnum = ErrorCode.NAME_SPECIAL_CHAR;
			}
			return SCMessage.newBuilder()
					.setRoleRenameResponse(RoleRenameResponse.newBuilder().setErrorCode(errorCodeEnum.getNumber()))
					.build();
		}

		if (role.getRenameCount() == 0) {
			clanService.joinClan(role, clanId);
		}
		role.setRenameCount(role.getRenameCount() + 1);

		return SCMessage.newBuilder().setRoleRenameResponse(RoleRenameResponse.newBuilder()).build();
	}

}
