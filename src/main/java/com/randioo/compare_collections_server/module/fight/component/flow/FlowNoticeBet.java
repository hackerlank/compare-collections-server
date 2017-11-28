/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.quartz.QuartzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 通知押注
 *
 * @author zsy
 * @Description:
 * @date 2017年9月22日 上午10:37:34
 */
@Component
public class FlowNoticeBet implements Flow {
    @Autowired
    private EventBus eventBus;

    @Autowired
    private GameBroadcast broadcast;

    @Autowired
    private QuartzManager quartzManager;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Override
    public void execute(Game game, String[] params) {
        int seat = game.getCurrentSeat();
        game.callTypeList.clear();
        game.callTypeList.add(CallTypeEnum.BET);
        Fight.SCFightNoticeBet scFightNoticeBet = Fight.SCFightNoticeBet.newBuilder().setSeat(seat).build();
        SC sc = SC.newBuilder().setSCFightNoticeBet(scFightNoticeBet).build();

       // if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            Map<String, Object> map = new HashMap<>();
            map.put("game", game);
            map.put("roleGameInfo", roleGameInfo);
            map.put("seat",roleGameInfo.seat);
            //quartzManager.addJob(BetJob.class, GlobleClass._G.wait_time, roleGameInfo.gameRoleId, map);
   //     }
        // 庄家不能通知
        broadcast.broadcast(game, sc);

    }

}
