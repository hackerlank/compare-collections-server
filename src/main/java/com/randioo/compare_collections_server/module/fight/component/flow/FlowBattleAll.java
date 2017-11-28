package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.ZjhComparator.CardsType;
import com.randioo.compare_collections_server.module.fight.component.ZjhComparator.Type;
import com.randioo.compare_collections_server.module.fight.component.ZjhComparator.Utils;
import com.randioo.compare_collections_server.module.fight.component.ZjhComparator.ZjhComparator;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightAllSeat;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 全部开牌比赛
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowBattleAll implements Flow {

	@Autowired
	private RoleGameInfoManager roleGameInfoManager;
	@Autowired
	private GameBroadcast gameBroadcast;

	@Override
	public void execute(Game game, String[] params) {
		List<RoleGameInfo> list = new ArrayList<>();
		for (String seatStr : params) {
			int seat = Integer.parseInt(seatStr);
			list.add(roleGameInfoManager.get(game, seat));
		}
		// 如果最后比牌的队列只有两个人的话走比较器
		if (list.size() == 2) {
			Collections.sort(list, new ZjhComparator());
			RoleGameInfo info = roleGameInfoManager.get(game, list.get(list.size() - 1).seat);
			info.chipMoney += game.betPool;
			for (int i = 0; i < list.size(); i++) {
				SC scFightAllSeat = SC.newBuilder()
						.setSCFightAllSeat(SCFightAllSeat.newBuilder().setSeat(list.get(list.size() - 1).seat)).build();
				SessionUtils.sc(list.get(i).roleId, scFightAllSeat);
			}
		}
		// 由于特殊牌的原因，特殊处理235和豹子
		if (list.size() > 2) {
			Map<Type, RoleGameInfo> map = new HashMap<>();
			for (RoleGameInfo info : list) {
				// 拿到所有的牌型
				Type type = CardsType.type(Utils.getNum(info.cards), Utils.getColor(info.cards));
				map.put(type, info);
			}
			if (map.containsValue(Type.BAOZI) && map.containsValue(Type.TESHU)) {
				RoleGameInfo info = roleGameInfoManager.get(game, map.get(Type.TESHU).seat);
				info.chipMoney += game.betPool;
				SC scFightAllSeat = SC.newBuilder().setSCFightAllSeat(SCFightAllSeat.newBuilder().setSeat(info.seat))
						.build();
				gameBroadcast.broadcast(game, scFightAllSeat);
			} else {
				Collections.sort(list, new ZjhComparator());
				RoleGameInfo info = roleGameInfoManager.get(game, list.get(list.size() - 1).seat);
				info.chipMoney += game.betPool;
				for (int i = 0; i < list.size(); i++) {
					SC scFightAllSeat = SC.newBuilder()
							.setSCFightAllSeat(SCFightAllSeat.newBuilder().setSeat(list.get(list.size() - 1).seat))
							.build();
					SessionUtils.sc(list.get(i).roleId, scFightAllSeat);
				}
			}
		}

	}
}
