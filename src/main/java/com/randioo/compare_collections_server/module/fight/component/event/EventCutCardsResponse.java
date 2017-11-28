package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;

public class EventCutCardsResponse implements IGameEvent {

    public EventCutCardsResponse(String gameRoleId, Game game) {
        super();
        this.gameRoleId = gameRoleId;
        this.game = game;
    }

    public String gameRoleId;
    public Game game;

    @Override
    public Game getGame() {
        return game;
    }

}
