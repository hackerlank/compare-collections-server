package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class EventNoticeCallType implements IGameEvent {

    public EventNoticeCallType(Game game, SC sc, String gameRoleId) {
        this.sc = sc;
        this.game = game;
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
