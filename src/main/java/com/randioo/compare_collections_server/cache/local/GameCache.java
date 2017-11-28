package com.randioo.compare_collections_server.cache.local;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.randioo_server_base.template.Function;

public class GameCache {
    private static Map<Integer, Game> gameMap = new ConcurrentHashMap<>();
    private static Map<String, Integer> gameLockMap = new ConcurrentHashMap<>();


    private static Map<String, Function> roundOverFunctionMap = new HashMap<>();

    private static Map<String, ICompareGameRule<Game>> ruleMap = new HashMap<>();

    private static List<Integer> goldModeGameId = new LinkedList<>();

    public static List<Integer> getGoldModeGameIdList() {
        return goldModeGameId;
    }

    public static Map<String, Integer> getGameLockMap() {
        return gameLockMap;
    }

//    public static Game getGameById(int gameId) {
//        if (gameId >= 200000) {
//            return goldModeGameMap.get(gameId);
//        } else {
//            return gameMap.get(gameId);
//        }
//    }

    public static Map<Integer, Game> getGameMap() {
        return gameMap;
    }

    public static Map<String, Integer> getGameLockStringMap() {
        return gameLockMap;
    }

    public static Map<String, Function> getRoundOverFunctionMap() {
        return roundOverFunctionMap;
    }

    public static Map<String, ICompareGameRule<Game>> getRuleMap() {
        return ruleMap;
    }

}
