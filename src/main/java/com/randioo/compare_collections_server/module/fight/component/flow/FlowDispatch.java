/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.cache.file.ZJHCardConfigCache;
import com.randioo.compare_collections_server.entity.file.ZJHCardConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.dispatch.CardPart;
import com.randioo.compare_collections_server.module.fight.component.dispatch.Dispatcher;
import com.randioo.compare_collections_server.module.fight.component.dispatch.RandomDispatcher;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Entity;
import com.randioo.compare_collections_server.protocol.Entity.RoleCardData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightDispatch;
import com.randioo.compare_collections_server.protocol.Fight.SCFightDispatch.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * @author zsy
 * @Description:
 * @date 2017年9月22日 下午3:27:29
 */
@Component
public class FlowDispatch implements Flow {

	@Autowired
	private RandomDispatcher randomDispatcher;

	@Autowired
	private RoleGameInfoManager roleGameInfoManager;

	@Autowired
	private AudienceManager audienceManager;

	@Override
	public void execute(Game game, String[] params) {
		int partCount = Integer.parseInt(params[0]);
		int everyPartCount = Integer.parseInt(params[1]);
		boolean sort = Boolean.parseBoolean(params[2]);
		boolean isWatch = Boolean.parseBoolean(params[3]);
		boolean ownLook = Boolean.parseBoolean(params[4]);
		boolean zjh = Boolean.parseBoolean(params[5]);
		List<Integer> remainCards = game.getRemainCards();
		List<Integer> cards = game.getRule().getCards();
		// 填充牌组
		remainCards.addAll(cards);
		Dispatcher dispatcher = null;
		dispatcher = randomDispatcher;

		List<CardPart> cardParts = dispatcher.dispatch(remainCards, partCount, everyPartCount);
		game.logger.info("发牌时roleMap: {}",game.getRoleIdMap());
		game.logger.info("要发{}份牌",cardParts.size());
		for (int i = 0; i < game.getRoleIdMap().size(); i++) {
			RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, i);
			CardPart cardPart = cardParts.get(i);
			roleGameInfo.cards.addAll(cardPart);
			game.logger.info("roleGameInfo:{} cards:{}", roleGameInfo.gameRoleId, roleGameInfo.cards);
			game.logger.debug("roleGameInfo:{} cards:{}", roleGameInfo.gameRoleId, roleGameInfo.cards);
		}

		// 每个玩家卡牌排序
		if (sort) {
			for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
				Collections.sort(roleGameInfo.cards);
			}
		}

		if (zjh) {
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				List<Integer> list = new ArrayList<>();
				Map<Integer, Integer> map = new HashMap<>();
				for (int i = 0; i < info.cards.size(); i++) {
					Map<Integer, ZJHCardConfig> cardMap = ZJHCardConfigCache.getZJHCardMap();
					ZJHCardConfig card = cardMap.get(info.cards.get(i));
					list.add(card.sorter);
					map.put(card.sorter, card.id);
				}
				// 进行对map的value值进行排序
				Collections.sort(list);
				info.cards.clear();
				for (int i = list.size() - 1; i >= 0; i--) {
					info.cards.add(map.get(list.get(i)));
				}
			}
		}

		// 必须和上面的for循环 i<game.getRoleIdMap().size() 一样
		for (int i = 0; i < game.getRoleIdMap().size(); i++) {
			SCFightDispatch.Builder dispatchBuilder = SCFightDispatch.newBuilder();
			RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, i);
			CardPart cardPart = cardParts.get(i);
			// 给自己的牌赋值
			if (!ownLook) {
				// 把所有的牌置为0
				for (int n = 0; n < cardPart.size(); n++) {
					cardPart.set(n, 0);
				}
			}
			dispatchBuilder
					.addRoleCardData(Entity.RoleCardData.newBuilder().addAllCard(cardPart).setSeat(roleGameInfo.seat));
			// 给别人的牌赋值
			for (int j = 0; j < game.getRoleIdMap().size(); j++) {
				RoleGameInfo otherRoleGameInfo = roleGameInfoManager.get(game, j);
				if (otherRoleGameInfo.seat == roleGameInfo.seat) {
					continue;
				}
				List<Integer> otherCardPart = new ArrayList<>(cardParts.get(i));
				if (!isWatch) {
					// 把所有的牌置为0
					for (int k = 0; k < otherCardPart.size(); k++) {
						otherCardPart.set(k, 0);
					}
				}
				dispatchBuilder.addRoleCardData(
						Entity.RoleCardData.newBuilder().addAllCard(otherCardPart).setSeat(otherRoleGameInfo.seat));
			}
			// 发送
			SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder().setSCFightDispatch(dispatchBuilder).build());
		}
		NoticeAudience(game);
	}

	private void NoticeAudience(Game game) {
		// 通知观众
		for (int roleId : audienceManager.getAudiences(game.getGameId())) {
			Builder audienceBuilder = SCFightDispatch.newBuilder();
			for (RoleGameInfo info : game.getRoleIdMap().values()) {
				// 玩家的牌
				ArrayList<Integer> audienceCards = new ArrayList<>();
				for (int i = 0; i < info.cards.size(); i++) {
					audienceCards.add(0);
				}
				audienceBuilder.addRoleCardData(RoleCardData.newBuilder().setSeat(info.seat).addAllCard(audienceCards));
			}
			SessionUtils.sc(roleId, SC.newBuilder().setSCFightDispatch(audienceBuilder).build());
		}
	}

}