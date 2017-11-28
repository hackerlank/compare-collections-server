package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;

public class EventReadyResponse implements IGameEvent {

    public EventReadyResponse(Game game, String gameRoleId) {
        super();
        this.game = game;
        this.gameRoleId = gameRoleId;
    }

    private Game game;
    public String gameRoleId;

    @Override
    public Game getGame() {
        return game;
    }
}
