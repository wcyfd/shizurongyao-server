package com.randioo.shizurongyao_server.module.rank.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;

public interface RankService extends ObserveBaseServiceInterface {

	GeneratedMessage showRank(Role role, int clanId);

	void refreshPointRank();

}
