package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.AutoPointNPCConfigCache;

public class AutoPointNPCConfig{
	public static final String urlKey="point_player.tbl";
	/** 地图编号 */
	public int level;
	/** 队伍编号 */
	public int mapId;
	/** 权重 */
	public int weight;
	/** 积分 */
	public int point;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			AutoPointNPCConfig config = new AutoPointNPCConfig();
			config.level=buffer.getInt();
			config.mapId=buffer.getInt();
			config.weight=buffer.getInt();
			config.point=buffer.getInt();
			
			AutoPointNPCConfigCache.putConfig(config);
		}
	}
}
