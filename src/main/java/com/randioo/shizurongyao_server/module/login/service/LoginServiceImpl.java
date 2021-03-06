package com.randioo.shizurongyao_server.module.login.service;

import java.util.List;
import java.util.Map;

import org.apache.mina.core.session.IoSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.db.DBRunnable;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.entity.RoleInterface;
import com.randioo.randioo_server_base.module.login.LoginCreateInfo;
import com.randioo.randioo_server_base.module.login.LoginHandler;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.module.login.LoginModelConstant;
import com.randioo.randioo_server_base.module.login.LoginModelService;
import com.randioo.randioo_server_base.utils.StringUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;
import com.randioo.randioo_server_base.utils.game.IdClassCreator;
import com.randioo.randioo_server_base.utils.service.ObserveBaseService;
import com.randioo.randioo_server_base.utils.system.GlobleConfig;
import com.randioo.randioo_server_base.utils.system.GlobleConfig.GlobleEnum;
import com.randioo.randioo_server_base.utils.template.Ref;
import com.randioo.shizurongyao_server.cache.local.ClanCache;
import com.randioo.shizurongyao_server.dao.CardDao;
import com.randioo.shizurongyao_server.dao.RoleDao;
import com.randioo.shizurongyao_server.dao.StoreVideoDao;
import com.randioo.shizurongyao_server.dao.WarBuildDao;
import com.randioo.shizurongyao_server.dao.WarChapterDao;
import com.randioo.shizurongyao_server.entity.bo.Card;
import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.po.CardList;
import com.randioo.shizurongyao_server.entity.po.OwlofwarGame;
import com.randioo.shizurongyao_server.module.card.service.CardService;
import com.randioo.shizurongyao_server.module.login.LoginConstant;
import com.randioo.shizurongyao_server.module.market.service.MarketService;
import com.randioo.shizurongyao_server.module.role.service.RoleService;
import com.randioo.shizurongyao_server.module.war.service.WarService;
import com.randioo.shizurongyao_server.protocol.Entity.CardData;
import com.randioo.shizurongyao_server.protocol.Entity.CardListData;
import com.randioo.shizurongyao_server.protocol.Entity.RoleData;
import com.randioo.shizurongyao_server.protocol.Error.ErrorCode;
import com.randioo.shizurongyao_server.protocol.Login.LoginCheckAccountResponse;
import com.randioo.shizurongyao_server.protocol.Login.LoginCreateRoleResponse;
import com.randioo.shizurongyao_server.protocol.Login.LoginGetRoleDataResponse;
import com.randioo.shizurongyao_server.protocol.ServerMessage.SCMessage;

@Service("loginService")
public class LoginServiceImpl extends ObserveBaseService implements LoginService {

	@Autowired
	private RoleDao roleDao;

	@Autowired
	private CardDao cardDao;

	@Autowired
	private CardService cardService;

	@Autowired
	private StoreVideoDao storeVideoDao;

	@Autowired
	private MarketService marketService;

	@Autowired
	private LoginModelService loginModelService;

	@Autowired
	private WarBuildDao warBuildDao;

	@Autowired
	private WarChapterDao warChapterDao;

	@Autowired
	private IdClassCreator idClassCreator;

	@Autowired
	private RoleService roleService;

	@Autowired
	private WarService warService;

	@Autowired
	private GameDB gameDB;

	@Override
	public void init() {
		// 初始化所有已经有过的帐号和昵称
		add(RoleCache.getNameSet(), roleDao.getAllNames());
		add(RoleCache.getAccountSet(), roleDao.getAllAccounts());

		loginModelService.setLoginHandler(new LoginHandlerImpl());
	}

	private void add(Map<String, String> map, List<String> list) {
		for (String str : list) {
			map.put(str, str);
		}
	}

	private class LoginHandlerImpl implements LoginHandler {

		@Override
		public boolean createRoleCheckAccount(LoginCreateInfo info, Ref<Integer> errorCode) {

			// 账号姓名不可为空
			if (StringUtils.isNullOrEmpty(info.getAccount())) {
				errorCode.set(LoginConstant.CREATE_ROLE_NAME_SENSITIVE);
				return false;
			}

			return true;
		}

		@Override
		public RoleInterface createRole(LoginCreateInfo loginCreateInfo) {
			String account = loginCreateInfo.getAccount();
			// 用户数据
			// 创建用户
			Role role = new Role();
			role.setAccount(account);

			roleService.newRoleInit(role);
			cardService.newRoleInit(role);
			marketService.newRoleInit(role);
			warService.newRoleInit(role);

			gameDB.getInsertPool().submit(new DBRunnable<Role>(role) {

				@Override
				public void run(Role t) {
					try {
						roleDao.insert(t);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			return role;
		}

		@Override
		public RoleInterface getRoleInterface(LoginInfo loginInfo) {
			return getRoleByAccount(loginInfo.getAccount());
		}

		@Override
		public boolean canSynLogin() {
			return true;
		}

		@Override
		public RoleInterface getRoleInterfaceFromDBById(int roleId) {
			return roleDao.get(null, roleId);
		}

		@Override
		public RoleInterface getRoleInterfaceFromDBByAccount(String account) {
			return roleDao.get(account, 0);
		}

		@Override
		public void loginRoleModuleDataInit(RoleInterface roleInterface) {
			// 将数据库中的数据放入缓存中
			Role role = (Role) roleInterface;

			roleService.roleInit(role);
			cardService.cardInit(role);
			warService.warInit(role);
			marketService.marketInit(role);
		}

	}

	@Override
	public GeneratedMessage getRoleData(String account, IoSession ioSession) {
		LoginInfo loginInfo = new LoginInfo();
		loginInfo.setAccount(account);

		Ref<Integer> errorCode = new Ref<>();

		RoleInterface roleInterface = loginModelService.getRoleData(loginInfo, errorCode, ioSession);

		if (roleInterface != null) {
			Role role = (Role) roleInterface;
			SCMessage sc = SCMessage
					.newBuilder()
					.setLoginGetRoleDataResponse(
							LoginGetRoleDataResponse.newBuilder().setServerTime(TimeUtils.getNowTime())
									.setRoleData(getRoleData(role))).build();
			return sc;
		}

		ErrorCode errorEnum = null;
		switch (errorCode.get()) {
		case LoginModelConstant.GET_ROLE_DATA_NOT_EXIST:
			errorEnum = ErrorCode.NO_ROLE_DATA;
			break;
		case LoginModelConstant.GET_ROLE_DATA_IN_LOGIN:
			errorEnum = ErrorCode.IN_LOGIN;
			break;
		}
		SCMessage sc = SCMessage.newBuilder()
				.setLoginGetRoleDataResponse(LoginGetRoleDataResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
				.build();
		return sc;
	}

	@Override
	public GeneratedMessage creatRole(String account) {
		LoginCreateInfo loginInfo = new LoginCreateInfo();
		loginInfo.setAccount(account);

		Ref<Integer> errorCode = new Ref<>();
		boolean result = loginModelService.createRole(loginInfo, errorCode);
		if (result) {
			return SCMessage.newBuilder().setLoginCreateRoleResponse(LoginCreateRoleResponse.newBuilder()).build();
		}

		ErrorCode errorEnum = null;
		switch (errorCode.get()) {
		case LoginModelConstant.CREATE_ROLE_EXIST:
			errorEnum = ErrorCode.EXIST_ROLE;
			break;
		case LoginModelConstant.CREATE_ROLE_FAILED:
			errorEnum = ErrorCode.CREATE_FAILED;
			break;
		case LoginConstant.CREATE_ROLE_NAME_SENSITIVE:
			errorEnum = ErrorCode.NAME_SENSITIVE;
			break;
		case LoginConstant.CREATE_ROLE_NAME_REPEATED:
			errorEnum = ErrorCode.NAME_REPEATED;
			break;
		case LoginConstant.CREATE_ROLE_NAME_TOO_LONG:
			errorEnum = ErrorCode.NAME_TOO_LONG;
			break;
		}

		return SCMessage.newBuilder()
				.setLoginCreateRoleResponse(LoginCreateRoleResponse.newBuilder().setErrorCode(errorEnum.getNumber()))
				.build();
	}

	@Override
	public GeneratedMessage login(String account) {
		LoginInfo info = new LoginInfo();
		info.setAccount(account);
		if (!GlobleConfig.Boolean(GlobleEnum.LOGIN)) {
			return SCMessage
					.newBuilder()
					.setLoginCheckAccountResponse(
							LoginCheckAccountResponse.newBuilder().setErrorCode(ErrorCode.REJECT_LOGIN.getNumber()))
					.build();
		}

		boolean isNewAccount = loginModelService.login(info);

		return SCMessage
				.newBuilder()
				.setLoginCheckAccountResponse(
						LoginCheckAccountResponse.newBuilder().setErrorCode(
								isNewAccount ? ErrorCode.NO_ROLE_ACCOUNT.getNumber() : ErrorCode.OK.getNumber()))
				.build();
	}

	private RoleData getRoleData(Role role) {
		RoleData.Builder roleDataBuilder = RoleData.newBuilder().setRoleId(role.getRoleId()).setName(role.getName())
				.setMoney(role.getMoney()).setPoint(role.getPoint()).setGold(role.getGold());
		for (Card card : role.getCardMap().values()) {
			roleDataBuilder.addCardDatas(CardData.newBuilder().setCardId(card.getCardId()).setLv(card.getLv())
					.setNum(card.getNum()));
		}
		Map<Integer, CardList> cardListMap = role.getCardListMap();
		for (CardList cardList : cardListMap.values()) {
			roleDataBuilder.addCardListDatas(CardListData.newBuilder().setMainId(cardList.getMainId())
					.setCardListIndex(cardList.getIndex()).addAllCardIds(cardList.getList()));
		}

		boolean isInFight = false;
		// 是否正在比赛
		OwlofwarGame game = role.getOwlofwarGame();
		if (game != null) {
			if (!game.isEnd()) {
				game.getLock();
				try {
					game.getLock().lock();
					if (!game.isEnd()) {
						isInFight = true;
					}
				} catch (Exception e) {
					e.printStackTrace();
				} finally {
					game.getLock().unlock();
				}
			}
		}
		roleDataBuilder.setInFight(isInFight);
		roleDataBuilder.setRenameCount(role.getRenameCount() >= 1);
		ClanMember clanMember = ClanCache.getClanMemberMap().get(role.getRoleId());
		roleDataBuilder.setClanId(clanMember == null ? 0 : clanMember.getClanId());
		return roleDataBuilder.build();
	}

	@Override
	public Role getRoleById(int roleId) {
		RoleInterface roleInterface = loginModelService.getRoleInterfaceById(roleId);
		return roleInterface == null ? null : (Role) roleInterface;
	}

	@Override
	public Role getRoleByAccount(String account) {
		RoleInterface roleInterface = loginModelService.getRoleInterfaceByAccount(account);
		return roleInterface == null ? null : (Role) roleInterface;
	}
}
