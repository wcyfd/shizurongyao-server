package com.randioo.shizurongyao_server.module.clan.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.ClanMember;
import com.randioo.shizurongyao_server.entity.bo.Role;

public interface ClanService extends ObserveBaseServiceInterface {

	ClanMember getClanMember(int roleId, int clanId);

	void joinClan(Role role, int clanId);

	GeneratedMessage showClan(Role role);

}
