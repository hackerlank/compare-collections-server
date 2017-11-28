package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.protocol.Fight.SCFightPublicScore;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

@Component
public class FlowNoticePublicScore implements Flow {

    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        gameBroadcast.broadcast(game,
                SC.newBuilder().setSCFightPublicScore(SCFightPublicScore.newBuilder().setScore(game.betPool)).build());
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("FlowNoticePublicScore [gameBroadcast=").append(gameBroadcast).append("]");
        return builder.toString();
    }

}
