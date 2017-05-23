package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.FamilyNameConfigCache;

public class FamilyNameConfig{
	public static final String urlKey="family_name.tbl";
	/** 编号 */
	public int clanId;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			FamilyNameConfig config = new FamilyNameConfig();
			config.clanId=buffer.getInt();
			
			FamilyNameConfigCache.putConfig(config);
		}
	}
}
