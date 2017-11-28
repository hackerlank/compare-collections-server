package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeReady;
import com.randioo.compare_collections_server.protocol.Fight.SCFightNoticeReady;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

@Component
public class FlowNoticeReady implements Flow {

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private EventBus eventBus;

    @Override
    public void execute(Game game, String[] params) {
        SC sc = SC.newBuilder().setSCFightNoticeReady(SCFightNoticeReady.newBuilder()).build();
        gameBroadcast.broadcast(game, sc);

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            EventNoticeReady event = new EventNoticeReady(game, sc, roleGameInfo.gameRoleId);
            eventBus.post(event);
        }
    }
}
