package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.YiyaAutoBetConfigCache;

public class YiyaAutoBetConfig{
	public static final String urlKey="yi_ya_auto_bet.tbl";
	/** id */
	public int id;
	/** 底注 */
	public int autoBet;
	/** 游戏类型 */
	public int gameType;
	/** 加注1 */
	public int bet1;
	/** 加注2 */
	public int bet2;
	/** 加注3 */
	public int bet3;
	/** 加注4 */
	public int bet4;
	/** 加注5 */
	public int bet5;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			YiyaAutoBetConfig config = new YiyaAutoBetConfig();
			config.id=buffer.getInt();
			config.autoBet=buffer.getInt();
			config.gameType=buffer.getInt();
			config.bet1=buffer.getInt();
			config.bet2=buffer.getInt();
			config.bet3=buffer.getInt();
			config.bet4=buffer.getInt();
			config.bet5=buffer.getInt();
			
			YiyaAutoBetConfigCache.putConfig(config);
		}
	}
}
