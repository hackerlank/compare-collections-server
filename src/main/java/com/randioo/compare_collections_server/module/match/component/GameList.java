package com.randioo.compare_collections_server.module.match.component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.randioo_server_base.utils.SpringContext;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author zsy
 * @Description: gamelist的下标表示有几个人
 * @create 2017-11-20 13:53
 **/
public class GameList extends ArrayList<List<Game>> {
    public GameList() {
        for (int i = 0; i < 6; i++) {
            this.add(i, new LinkedList<Game>());
        }
    }

    public void add(Game game) {
        int size = game.getRoleIdMap().size();
        AudienceManager audienceManager = SpringContext.getBean(AudienceManager.class);
        int audienceCount = audienceManager.getAudiences(game.getGameId()).size();
        size += audienceCount;
        if (size != 0 && size != 6) {
            this.get(size).add(game);
        }
    }

    public boolean hasGame(int count) {
        return this.get(count).size() != 0;
    }

    /**
     * 获得X个人的游戏
     *
     * @param count
     * @return
     */
    public Game getGameByCount(int count) {
        return this.get(count).get(0);
    }
}
