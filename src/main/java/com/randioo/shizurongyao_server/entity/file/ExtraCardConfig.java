package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.ExtraCardConfigCache;

public class ExtraCardConfig{
	public static final String urlKey="extra_card.tbl";
	/** 编号 */
	public int id;
	/** 名称 */
	public String name;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			ExtraCardConfig config = new ExtraCardConfig();
			config.id=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.name = new String(b);}
			
			ExtraCardConfigCache.putConfig(config);
		}
	}
}
