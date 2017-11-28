package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.ZJHCardTypeConfigCache;

public class ZJHCardTypeConfig{
	public static final String urlKey="zjh_card_type.tbl";
	/** 牌型ID */
	public int id;
	/** 扑克ID */
	public String cardType;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			ZJHCardTypeConfig config = new ZJHCardTypeConfig();
			config.id=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.cardType = new String(b);}
			
			ZJHCardTypeConfigCache.putConfig(config);
		}
	}
}
