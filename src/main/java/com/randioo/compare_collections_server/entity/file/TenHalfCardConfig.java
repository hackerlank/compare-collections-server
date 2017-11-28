package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.TenHalfCardConfigCache;

public class TenHalfCardConfig{
	public static final String urlKey="ten_half_card.tbl";
	/** ID */
	public int id;
	/** 点数 */
	public int point;
	/** 牌面 */
	public int number;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			TenHalfCardConfig config = new TenHalfCardConfig();
			config.id=buffer.getInt();
			config.point=buffer.getInt();
			config.number=buffer.getInt();
			
			TenHalfCardConfigCache.putConfig(config);
		}
	}
}
