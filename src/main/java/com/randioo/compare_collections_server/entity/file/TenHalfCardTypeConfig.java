package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.TenHalfCardTypeConfigCache;

public class TenHalfCardTypeConfig{
	public static final String urlKey="ten_half_card_type.tbl";
	/** ID */
	public int id;
	/** 显示名称 */
	public String cardType;
	/** 倍率 */
	public int rate;
	/** 数值 */
	public int number;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			TenHalfCardTypeConfig config = new TenHalfCardTypeConfig();
			config.id=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.cardType = new String(b);}
			config.rate=buffer.getInt();
			config.number=buffer.getInt();
			
			TenHalfCardTypeConfigCache.putConfig(config);
		}
	}
}
