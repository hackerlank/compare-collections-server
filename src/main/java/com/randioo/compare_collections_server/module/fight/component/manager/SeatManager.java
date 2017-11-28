/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.manager;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @Description: 获得各种座位号
 * @author zsy
 * @date 2017年9月22日 上午9:20:05
 */
@Component
public class SeatManager {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    /**
     * 获得下一个座位号
     * 
     * @param game
     * @param currentSeat
     * @return
     */
    public int getNext(Game game, int currentSeat) {
        int maxCount = game.getSeatMap().size();
        int seat = currentSeat + 1;
        return seat == maxCount ? 0 : seat;
    }

    public int getNext(Game game) {
        return getNext(game, game.getCurrentSeat());
    }

    /**
     * 获得前一个座位号
     * 
     * @param game
     * @param currentSeat
     * @return
     * @author wcy 2017年10月10日
     */
    public int getPrevious(Game game, int currentSeat) {
        int maxCount = game.getSeatMap().size();
        currentSeat--;
        return currentSeat < 0 ? maxCount - 1 : currentSeat;
    }

    public int getPrevious(Game game) {
        return getPrevious(game, game.getCurrentSeat());
    }

    public int getSeatByRoleId(Game game, int roleId) {
        String gameRoleId = roleGameInfoManager.getGameRoleId(game, roleId);
        RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
        return roleGameInfo.seat;
    }

    /**
     * 获得空位
     * 
     * @param game
     * @param maxCount
     * @return
     * @author wcy 2017年9月23日
     */
    public int getEmptySeat(Game game, int maxCount) {
        Map<Integer, RoleGameInfo> seatMap = game.getSeatMap();
        int emptySeat = -1;
        for (int i = 0; i < maxCount; i++) {
            RoleGameInfo roleGameInfo = seatMap.get(i);
            if (roleGameInfo == null) {
                emptySeat = i;
                break;
            }
        }

        return emptySeat;
    }
}
