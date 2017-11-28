package com.randioo.compare_collections_server.module.fight.component.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.base.IGameEvent;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class EventFollow implements IGameEvent {

    public EventFollow(SC sc, String gameRoleId, Game game) {
        super();
        this.sc = sc;
        this.gameRoleId = gameRoleId;
        this.game = game;
    }

    public SC sc;
    public String gameRoleId;
    public Game game;

    @Override
    public Game getGame() {
        return game;
    }

}
