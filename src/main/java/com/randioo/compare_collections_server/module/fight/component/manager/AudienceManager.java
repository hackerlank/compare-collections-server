package com.randioo.compare_collections_server.module.fight.component.manager;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.randioo_server_base.cache.RoleCache;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AudienceManager {

    private ConcurrentHashMap<Integer, LinkedList<Integer>> audiencesMap = new ConcurrentHashMap<>();

    public LinkedList<Integer> getAudiences(int gameId) {
        if (!audiencesMap.containsKey(gameId)) {
            audiencesMap.putIfAbsent(gameId, new LinkedList<Integer>());
        }
        return audiencesMap.get(gameId);
    }

    public void add(int gameId, int roleId) {
        List<Integer> list = getAudiences(gameId);
        list.add(roleId);
    }

    public boolean isAudience(int roleId, int gameId) {
        return getAudiences(gameId).contains(roleId);
    }

    public void remove(int roleId, int gameId) {
        LinkedList<Integer> audiences = getAudiences(gameId);
        audiences.remove(Integer.valueOf(roleId));

        Role role = RoleCache.getRoleById(roleId);
        role.setGameId(0);
    }

    /**
     * 获得推测的座位号
     *
     * @param roleId
     * @param game
     * @return
     */
    public int getSeat(int roleId, Game game) {
        LinkedList<Integer> audiences = getAudiences(game.getGameId());
        int index = audiences.indexOf(roleId);
        int roleGameInfoCount = game.getRoleIdMap().values().size();
        return roleGameInfoCount + index;
    }

    /**
     * @param gameId
     * @param count
     * @return
     * @author wcy 2017年11月22日
     */
    public List<Integer> extractCount(int gameId, int count) {
        List<Integer> result = new ArrayList<>();
        LinkedList<Integer> audiences = getAudiences(gameId);
        if (audiences.size() == 0) {
            return result;
        }

        int toIndex = Math.min(count, audiences.size());
        for (int i = toIndex - 1; i >= 0; i--) {
            int value = audiences.remove(i);
            result.add(0, value);
        }
        return result;
    }

    public void destoryAudienceSeats(int gameId) {
        audiencesMap.remove(gameId);
    }

    public static void main(String[] args) {
        AudienceManager m = new AudienceManager();
        for (int i = 0; i < 10; i++) {
            m.add(1, i);
        }

        List<Integer> result = m.extractCount(1, 6);
        System.out.println(result);
        System.out.println(m.getAudiences(1));
    }
}
