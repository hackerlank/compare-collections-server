package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class EventGameStart implements IGameEvent {
    private SC sc;
    private Game game;

    public EventGameStart(Game game, SC sc) {
        this.game = game;
        this.sc = sc;
    }

    @Override
    public Game getGame() {
        return game;
    }

}
