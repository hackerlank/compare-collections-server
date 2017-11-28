package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class EventNoticeReady implements IGameEvent {

    public EventNoticeReady(Game game, SC sc, String gameRoleId) {
        this.game = game;
        this.sc = sc;
        this.gameRoleId = gameRoleId;
    }

    private Game game;
    public SC sc;
    public String gameRoleId;

    @Override
    public Game getGame() {
        return game;
    }

}
