package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.MapConfigCache;

public class MapConfig{
	public static final String urlKey="map_id.tbl";
	/** 地图编号 */
	public int mapId;
	/** 卡牌编号 */
	public int mainId;
	/** 使用扩展卡牌 */
	public int cardNpcType;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			MapConfig config = new MapConfig();
			config.mapId=buffer.getInt();
			config.mainId=buffer.getInt();
			config.cardNpcType=buffer.getInt();
			
			MapConfigCache.putConfig(config);
		}
	}
}
