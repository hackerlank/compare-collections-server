package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.cache.file.YiyaAutoBetConfigCache;
import com.randioo.compare_collections_server.entity.file.YiyaAutoBetConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.protocol.Fight.SCFightBetScore;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zsy
 * @Description: 十点半金币场通知押注
 * @create 2017-11-28 17:38
 **/
@Component
public class FlowGoldModeNoticeBet implements Flow {
    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        YiyaAutoBetConfig betConfig = YiyaAutoBetConfigCache.getYiYaAutoBetMap().get(game.matchParameter);
        //自动压的筹码
        int bet = betConfig.autoBet;

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            if (roleGameInfo.seat == game.getZhuangSeat()) {
                continue;
            }
            roleGameInfo.betScore = bet;
            SCFightBetScore scFightCallScore = SCFightBetScore.newBuilder().setSeat(roleGameInfo.seat).setScore(bet)
                    .build();
            SC scCallScore = SC.newBuilder().setSCFightBetScore(scFightCallScore).build();
            gameBroadcast.broadcast(game, scCallScore);
        }


    }
}
