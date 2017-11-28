/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.EventGameStart;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightStart;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 通知发牌
 *
 * @author zsy
 * @Description:
 * @date 2017年9月22日 下午2:15:41
 */
@Component
public class FlowNoticeGameStart implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private EventBus eventBus;

    @Override
    public void execute(Game game, String[] params) {
        GameConfigData config = game.getGameConfig();
        SCFightStart.Builder scFightStartBuilder = SCFightStart.newBuilder();
        scFightStartBuilder.setMaxRound(config.getRoundCount());
        scFightStartBuilder.setZhuangSeat(game.getZhuangSeat());
        scFightStartBuilder.setCurrentRoundNum(game.getFinishRoundCount());
        scFightStartBuilder.setRemainCardCount(game.getRule().getCards().size());
        SCFightStart scFightStart = scFightStartBuilder.build();

        SC sc = SC.newBuilder().setSCFightStart(scFightStart).build();
        
        gameBroadcast.broadcast(game, sc);
        
        eventBus.post(new EventGameStart(game, sc));

    }

}
