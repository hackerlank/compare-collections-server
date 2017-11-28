package com.randioo.compare_collections_server.module.match.component;

import java.util.concurrent.ExecutionException;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;

public interface MatchStrategy {
    Game getGame(Role role) throws ExecutionException, InterruptedException;
}
