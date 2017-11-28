package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.CXCardListConfigCache;

public class CXCardListConfig{
	public static final String urlKey="cx_card_list.tbl";
	/** ID */
	public int id;
	/** 牌面 */
	public int cardNum;
	/** 数字战力 */
	public int num;
	/** 花战力 */
	public int color;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			CXCardListConfig config = new CXCardListConfig();
			config.id=buffer.getInt();
			config.cardNum=buffer.getInt();
			config.num=buffer.getInt();
			config.color=buffer.getInt();
			
			CXCardListConfigCache.putConfig(config);
		}
	}
}
