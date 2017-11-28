package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightOpenCardsResult;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 分牌后将所有牌给所有玩家看到
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowOpenCards implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        // 参与最后切牌的玩家座位
        for (String seatStr : params) {
            int seat = Integer.parseInt(seatStr);
            RoleGameInfo info = roleGameInfoManager.get(game, seat);

            SC scOpenResult = SC.newBuilder()
                    .setSCFightOpenCardsResult(
                            SCFightOpenCardsResult.newBuilder().addAllCards(info.cards).setSeat(info.seat))
                    .build();

            // 通知
            gameBroadcast.broadcast(game, scOpenResult);
        }
    }
}
