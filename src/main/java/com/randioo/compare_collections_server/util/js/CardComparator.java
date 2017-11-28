package com.randioo.compare_collections_server.util.js;

import java.util.Comparator;

public class CardComparator implements Comparator<CardConfig> {

	@Override
	public int compare(CardConfig o1, CardConfig o2) {
		if(o1.value> o2.value){
			return 1 ;
		}else if(o1.value< o2.value) {
			return -1 ;
		}else{
			if(o1.hua>o2.hua){
				return 1 ;
			}else{
				return -1 ;
			}
		}
	}

}
