package com.randioo.compare_collections_server.module.fight.component.event.base;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.randioo_server_base.eventbus.Event;

public interface IGameEvent extends Event {
    Game getGame();
}
