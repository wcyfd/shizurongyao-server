package com.randioo.shizurongyao_server.entity.po;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.randioo.randioo_server_base.utils.GameUtils;
import com.randioo.randioo_server_base.utils.ValueGet;
import com.randioo.randioo_server_base.utils.WeightGet;
import com.randioo.shizurongyao_server.cache.file.AutoPointNPCConfigCache;
import com.randioo.shizurongyao_server.entity.bo.Role;
import com.randioo.shizurongyao_server.entity.file.AutoPointNPCConfig;
import com.randioo.shizurongyao_server.formatter.ResourceFormatter;
import com.randioo.shizurongyao_server.module.fight.FightConstant.GameFightType;

public class FightEventListenerAdapter implements FightEventListener {
	private Role role;

	private Set<Resource> resources = new HashSet<>();

	public FightEventListenerAdapter(Role role) {
		this.role = role;
	}

	@Override
	public String getAward() {
		return ResourceFormatter.formatResources(resources);
	}

	@Override
	public void setAward(String award) {
		if (award == null)
			return;
		Set<Resource> formatResources = ResourceFormatter.formatResouces(award);
		ResourceFormatter.combineResource(formatResources, resources);
	}

	//
	// public int getAIMapIdByPoint() {
	// int point = getRole().getPoint();
	// List<AutoNpcConfig> list = AutoNpcConfigCache.getAllAutoNpcConfigList();
	// int mapsId = 0;
	// for (AutoNpcConfig config : list) {
	// if (point > config.getPoint())
	// continue;
	//
	// int index = RandomUtils.getRandomNum(config.getNpcIdList().size());
	// mapsId = config.getNpcIdList().get(index);
	// //mapsId = 1101;
	// return mapsId;
	//
	// }
	// mapsId = 0;
	// AutoNpcConfig config = list.get(list.size() - 1);
	// int index = RandomUtils.getRandomNum(config.getNpcIdList().size());
	// mapsId = config.getNpcIdList().get(index);
	// //mapsId = 1101;
	// return mapsId;
	// }

	private WeightGet<AutoPointNPCConfig> weightValue = new WeightGet<AutoPointNPCConfig>() {

		@Override
		public int getWeightValue(AutoPointNPCConfig param) {
			return param.weight;
		}
	};

	private ValueGet<AutoPointNPCConfig, Integer> valueGet = new ValueGet<AutoPointNPCConfig, Integer>() {

		@Override
		public Integer getValue(AutoPointNPCConfig param) {
			return param.mapId;
		}
	};

	public int getAIMapIdByPoint() {
		int point = getRole().getPoint();
		int mapsId = 0;
		Map<Integer, Map<Integer, AutoPointNPCConfig>> map = AutoPointNPCConfigCache.getAutoPointNPCMap();
		for (Integer standardPoint : map.keySet()) {
			if (point > standardPoint && standardPoint != -1) {
				continue;
			}
			List<AutoPointNPCConfig> list = new ArrayList<>(map.get(standardPoint).values());
			List<Integer> resultList = GameUtils.getRandomCountValuesByWeightValue(1, list, weightValue, valueGet);
			mapsId = resultList.get(0);
			break;
		}

		return mapsId;
	}

	@Override
	public Role getRole() {
		return role;
	}

	@Override
	public GameFightType getReturnType(Role role) {
		return GameFightType.TEST;
	}

	@Override
	public int getAI() {
		return 0;
	}

	@Override
	public int getPalaceLv() {

		return 0;
	}

	@Override
	public void enterGame(Role role) {
		// TODO Auto-generated method stub

	}

	@Override
	public void matchFighter(OwlofwarGame game, Role role) {
		// TODO Auto-generated method stub

	}

	@Override
	public void readyFight(OwlofwarGame game, Role role1, Role role2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void startFight(OwlofwarGame game, Role role1, Role role2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterEnd(OwlofwarGame game, byte result) {
		// TODO Auto-generated method stub

	}
}
