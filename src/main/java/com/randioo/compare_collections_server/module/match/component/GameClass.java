package com.randioo.compare_collections_server.module.match.component;

import com.randioo.compare_collections_server.cache.file.YiyaAutoBetConfigCache;
import com.randioo.compare_collections_server.entity.po.Game;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zsy
 * @Description: 金币场游戏分类
 * @create 2017-11-28 14:30
 **/
public class GameClass {
    private Map<Integer, GameList> gameClassMap = new HashMap<>();

    public GameClass() {
        for (int type : YiyaAutoBetConfigCache.getYiYaAutoBetMap().keySet()) {
            gameClassMap.put(type, new GameList());
        }
    }

    public void add(Game game) {
        GameList gameList = gameClassMap.get(game.matchParameter);
        gameList.add(game);
    }

    public GameList getGameList(Integer matchParameter) {
        return gameClassMap.get(matchParameter);
    }
}
