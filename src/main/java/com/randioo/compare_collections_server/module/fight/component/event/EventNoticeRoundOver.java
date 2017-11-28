package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class EventNoticeRoundOver implements IGameEvent {

    public EventNoticeRoundOver(Game game, SC sc) {
        this.sc = sc;
        this.game = game;
    }

    private Game game;
    public SC sc;

    @Override
    public Game getGame() {
        return game;
    }

}
