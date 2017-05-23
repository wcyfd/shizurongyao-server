package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.CardInitConfigCache;

public class CardInitConfig{
	public static final String urlKey="card_init.tbl";
	/** 卡牌编号 */
	public int cardId;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			CardInitConfig config = new CardInitConfig();
			config.cardId=buffer.getInt();
			
			CardInitConfigCache.putConfig(config);
		}
	}
}
