package com.randioo.shizurongyao_server.module.race.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.randioo_server_base.utils.service.ObserveBaseServiceInterface;
import com.randioo.shizurongyao_server.entity.bo.Role;

public interface RaceService extends ObserveBaseServiceInterface {

	GeneratedMessage showRace(Role role, int gameType);

	GeneratedMessage getRaceAward(Role role);

	GeneratedMessage signUp(Role role);

	GeneratedMessage cancelSignUp(Role role);

	void startRace(int raceId);

	void pointRace(Integer size, Integer times);

	GeneratedMessage getRaceRoleNum(Role role);

	void sendRaceAward();

	GeneratedMessage showNextRace(Role role);
}
