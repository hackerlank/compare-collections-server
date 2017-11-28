package com.randioo;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardComparator;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardTypeGetter;
import com.randioo.randioo_server_base.config.ConfigLoader;

public class TenHalfCardTypeTest extends SpringTestBase {
	@Autowired
	private CardComparator cardComparator;

	@Autowired
	private CardTypeGetter cardTypeGetter;

	@Test
	public void test() {
		ConfigLoader.loadConfig("com.randioo.compare_collections_server.entity.file", "./config.zip");

		List<Integer> zhuangCards = Arrays.asList(10, 15);
		List<Integer> xianCards = Arrays.asList(10, 34, 15);

		System.out.println("庄家 " + (cardComparator.compare(zhuangCards, xianCards) ? "赢" : "输"));
	}

	@Test
	public void cardTypeTest() {
		ConfigLoader.loadConfig("com.randioo.compare_collections_server.entity.file", "./config.zip");
		List<Integer> xianCards = Arrays.asList(24);
		TenHalfCardTypeConfig cardType = cardTypeGetter.get(xianCards);
		System.out.println(cardType.cardType);
	}

}
