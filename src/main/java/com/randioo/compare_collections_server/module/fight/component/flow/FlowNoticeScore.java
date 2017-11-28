package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.parser.ScoreProtoParser;
import com.randioo.compare_collections_server.protocol.Fight.SCFightScore;
import com.randioo.compare_collections_server.protocol.Fight.SCFightScore.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

@Component
public class FlowNoticeScore implements Flow {

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private ScoreProtoParser scoreProtoParser;

    @Override
    public void execute(Game game, String[] params) {
        // 分数通知
        Builder scoreBuilder = SCFightScore.newBuilder();

        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            scoreBuilder.addScoreData(scoreProtoParser.parse(roleGameInfo));
        }
        gameBroadcast.broadcast(game, SC.newBuilder().setSCFightScore(scoreBuilder).build());
    }

}
