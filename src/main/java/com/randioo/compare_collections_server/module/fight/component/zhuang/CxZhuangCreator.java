/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.zhuang;

import com.randioo.compare_collections_server.GlobleConstant;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.FightConstant;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.utils.RandomUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author zsy
 * @Description:
 * @date 2017年9月21日 下午5:49:50
 */
@Component
public class CxZhuangCreator implements ZhuangCreator {
    @Autowired
    MatchService matchService;

    @Autowired
    SeatManager seatManager;

    @Override
    public int getSeat(Game game) {
        int seat = -1;
        if (game.envVars.Boolean(GlobleConstant.ARGS_ZHUANG)) {
            seat = 0;
        } else {
            int zhuangType = game.getGameConfig().getZhuangType();

            if (zhuangType == FightConstant.ZHUANG_GOLD) {
                Set<Integer> seats = game.getSeatMap().keySet();
                List<Integer> seatList = new ArrayList<>(seats);
                int index = RandomUtils.getRandomNum(0, seatList.size() - 1);
                seat = seatList.get(index);
            } else if (zhuangType == FightConstant.ZHUANG_ROOM_OWNER) {
                seat = seatManager.getSeatByRoleId(game, game.getMasterRoleId());
            } else if (zhuangType == FightConstant.ZHUANG_ORDER) {
                //自由轮庄
                if (game.getFinishRoundCount() == 0) {
                    //第一局房主是庄家
                    seat = seatManager.getSeatByRoleId(game, game.getMasterRoleId());
                } else {
                    // 上一把的庄家
                    int preZhuangSeat = game.getZhuangSeat();
                    seat = seatManager.getNext(game, preZhuangSeat);
                }
            }
        }
        return seat;
    }
}
