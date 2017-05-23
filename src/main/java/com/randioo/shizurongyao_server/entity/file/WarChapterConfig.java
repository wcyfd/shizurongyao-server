package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.WarChapterConfigCache;

public class WarChapterConfig{
	public static final String urlKey="warchapter.tbl";
	/** 章节id */
	public int chapterId;
	/** 章节银币奖励 */
	public int moneyAward;
	/** 卡牌奖励,(cardId,num) */
	public String cardAward;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			WarChapterConfig config = new WarChapterConfig();
			config.chapterId=buffer.getInt();
			config.moneyAward=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.cardAward = new String(b);}
			
			WarChapterConfigCache.putConfig(config);
		}
	}
}
