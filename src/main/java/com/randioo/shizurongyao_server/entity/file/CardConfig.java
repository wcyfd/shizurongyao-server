package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.CardConfigCache;

public class CardConfig{
	public static final String urlKey="card.tbl";
	/** 编号 */
	public int cardId;
	/** 品质 */
	public int quality;
	/** 名字 */
	public String name;
	/** 卡牌类型 */
	public int cardType;
	/** 可上场卡牌数 */
	public int useTimes;
	/** 是否生效 */
	public int canUse;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			CardConfig config = new CardConfig();
			config.cardId=buffer.getInt();
			config.quality=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.name = new String(b);}
			config.cardType=buffer.getInt();
			config.useTimes=buffer.getInt();
			config.canUse=buffer.getInt();
			
			CardConfigCache.putConfig(config);
		}
	}
}
