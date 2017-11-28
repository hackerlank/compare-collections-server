package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.SpecialCardConfigCache;

public class SpecialCardConfig{
	public static final String urlKey="special_card_type.tbl";
	/** ID */
	public int id;
	/** 标识 */
	public String flag;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			SpecialCardConfig config = new SpecialCardConfig();
			config.id=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.flag = new String(b);}
			
			SpecialCardConfigCache.putConfig(config);
		}
	}
}
