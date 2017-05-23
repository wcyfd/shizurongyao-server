package com.randioo.shizurongyao_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.shizurongyao_server.cache.file.NPCPlayerConfigCache;

public class NPCPlayerConfig{
	public static final String urlKey="npc_player.tbl";
	/** npc编号 */
	public int teamId;
	/** 兵种编号 */
	public int cardId;
	/** 队伍名称 */
	public String teamName;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			NPCPlayerConfig config = new NPCPlayerConfig();
			config.teamId=buffer.getInt();
			config.cardId=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.teamName = new String(b);}
			
			NPCPlayerConfigCache.putConfig(config);
		}
	}
}
