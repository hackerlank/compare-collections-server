package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightCutCards;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 玩家进入分牌通知
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowFightCutCards implements Flow {
    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private EventBus eventBus;

    @Override
    public void execute(Game game, String[] params) {
        int seat = game.getCurrentSeat();
        SC scCutCards = SC.newBuilder().setSCFightCutCards(SCFightCutCards.newBuilder().setSeat(seat)).build();
        gameBroadcast.broadcast(game, scCutCards);

    }
}
