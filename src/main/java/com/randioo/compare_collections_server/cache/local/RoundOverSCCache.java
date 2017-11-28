package com.randioo.compare_collections_server.cache.local;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

public class RoundOverSCCache {
    private static Map<Integer, SC> map = new ConcurrentHashMap<>();

    public static void clear(int gameId) {
        map.remove(gameId);
    }

    public static void put(int gameId, SC roundOverSC) {
        map.put(gameId, roundOverSC);
    }

    public static SC get(int gameId) {
        return map.get(gameId);
    }
}
