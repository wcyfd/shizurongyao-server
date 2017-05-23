package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.WarBuildConfigCache;

public class WarBuildConfig{
	public static final String urlKey="warbuild.tbl";
	/** 建筑id */
	public int buildId;
	/** npc的队伍 */
	public int npcTeam;
	/** 奖励银币数量 */
	public int award;
	/** 战场编号 */
	public int chapterId;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			WarBuildConfig config = new WarBuildConfig();
			config.buildId=buffer.getInt();
			config.npcTeam=buffer.getInt();
			config.award=buffer.getInt();
			config.chapterId=buffer.getInt();
			
			WarBuildConfigCache.putConfig(config);
		}
	}
}
