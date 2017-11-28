package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.CXCardTypeConfigCache;

public class CXCardTypeConfig{
	public static final String urlKey="cx_card_type.tbl";
	/** ID */
	public int id;
	/** 显示名称 */
	public String name;
	/** 牌型 */
	public String cardType;
	/** 大小 */
	public int num;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			CXCardTypeConfig config = new CXCardTypeConfig();
			config.id=buffer.getInt();
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.name = new String(b);}
			{byte[] b = new byte[buffer.getShort()];buffer.get(b);config.cardType = new String(b);}
			config.num=buffer.getInt();
			
			CXCardTypeConfigCache.putConfig(config);
		}
	}
}
