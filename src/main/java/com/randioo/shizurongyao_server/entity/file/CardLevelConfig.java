package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.CardLevelConfigCache;

public class CardLevelConfig{
	public static final String urlKey="card_level.tbl";
	/** 品质 */
	public int quality;
	/** 等级 */
	public int level;
	/** 购买所需银币 */
	public int money;
	/** 升级所需卡牌数 */
	public int levelUpNeedCard;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			CardLevelConfig config = new CardLevelConfig();
			config.quality=buffer.getInt();
			config.level=buffer.getInt();
			config.money=buffer.getInt();
			config.levelUpNeedCard=buffer.getInt();
			
			CardLevelConfigCache.putConfig(config);
		}
	}
}
