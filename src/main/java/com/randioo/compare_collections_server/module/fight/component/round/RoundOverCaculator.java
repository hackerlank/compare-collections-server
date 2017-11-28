package com.randioo.compare_collections_server.module.fight.component.round;

import java.util.Map;

import com.randioo.compare_collections_server.entity.po.Game;

public interface RoundOverCaculator {
    public Map<Integer, RoundInfo> getRoundResult(Game game);
}
