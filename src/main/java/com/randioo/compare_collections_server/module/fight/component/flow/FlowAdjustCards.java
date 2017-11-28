package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeAdjustCards;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightCutCards;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 玩家进入分牌通知
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowAdjustCards implements Flow {

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private EventBus eventBus;

    @Override
    public void execute(Game game, String[] params) {
        for (String seatStr : params) {
            int seat = Integer.parseInt(seatStr);
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            SC scCutCards = SC.newBuilder()
                    .setSCFightCutCards(SCFightCutCards.newBuilder().setSeat(roleGameInfo.seat))
                    .build();
            SessionUtils.sc(roleGameInfo.roleId, scCutCards);

            eventBus.post(new EventNoticeAdjustCards(game, scCutCards, roleGameInfo.gameRoleId));
        }
    }
}
