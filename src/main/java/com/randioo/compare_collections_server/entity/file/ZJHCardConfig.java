package com.randioo.compare_collections_server.entity.file;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

import com.randioo.compare_collections_server.cache.file.ZJHCardConfigCache;

public class ZJHCardConfig{
	public static final String urlKey="zjh_card_list.tbl";
	/** ID */
	public int id;
	/** 牌面 */
	public int cardNum;
	/** 数字战力 */
	public int num;
	/** 花色 */
	public int color;
	/** 排序 */
	public int sorter;
		
	public static void parse(ByteBuffer buffer){
		buffer.order(ByteOrder.LITTLE_ENDIAN);
		while(buffer.hasRemaining()){
			ZJHCardConfig config = new ZJHCardConfig();
			config.id=buffer.getInt();
			config.cardNum=buffer.getInt();
			config.num=buffer.getInt();
			config.color=buffer.getInt();
			config.sorter=buffer.getInt();
			
			ZJHCardConfigCache.putConfig(config);
		}
	}
}
