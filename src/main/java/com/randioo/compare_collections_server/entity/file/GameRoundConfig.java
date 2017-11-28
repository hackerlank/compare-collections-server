package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.GameRoundConfigCache;

public class GameRoundConfig{
	public static final String urlKey="round.tbl";
	/** ID */
	public int id;
	/** 局数 */
	public int round;
	/** 燃点币 */
	public int needMoney;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			GameRoundConfig config = new GameRoundConfig();
			config.id=buffer.getInt();
			config.round=buffer.getInt();
			config.needMoney=buffer.getInt();
			
			GameRoundConfigCache.putConfig(config);
		}
	}
}
