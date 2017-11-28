/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.protocol.Fight.SCFightAutoBet;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 开局自动押注
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowAutoBet implements Flow {

    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        int baseScore = game.getGameConfig().getBet();
        boolean playerBetScoreRecord = Boolean.parseBoolean(params[0]);
        for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
            RoleGameInfo roleGameInfo = entrySet.getValue();
            roleGameInfo.chipMoney -= baseScore;
            // 池底总分数
            game.betPool += baseScore;
            if (playerBetScoreRecord) {
                roleGameInfo.betScoreRecord += baseScore;
            }
            SC scAutoBet = SC.newBuilder()
                    .setSCFightAutoBet(SCFightAutoBet.newBuilder().setCash(baseScore).setSeat(roleGameInfo.seat))
                    .build();
            // 通知
            gameBroadcast.broadcast(game, scAutoBet);
        }
    }
}
