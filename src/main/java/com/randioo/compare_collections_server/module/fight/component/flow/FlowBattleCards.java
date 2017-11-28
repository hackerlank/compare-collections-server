package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.protocol.Fight.SCFightBattleOutCards;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 最后推送参与了比牌的人的牌
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowBattleCards implements Flow {
	@Override
	public void execute(Game game, String[] params) {
		for (Map.Entry<RoleGameInfo, RoleGameInfo> maps : game.getBattleMap().entrySet()) {
			List<RoleGameInfo> roleGameInfos = new ArrayList<>();
			roleGameInfos.add(maps.getKey());
			roleGameInfos.add(maps.getValue());
			for (RoleGameInfo info : roleGameInfos) {
				SC scBattleCards = SC.newBuilder()
						.setSCFightBattleOutCards(
								SCFightBattleOutCards.newBuilder().setSeat(info.seat).addAllPlayerCards(info.cards))
						.build();
				for (RoleGameInfo infoer : roleGameInfos) {
					SessionUtils.sc(infoer.roleId, scBattleCards);
				}
			}
		}
	}
}
