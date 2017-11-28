package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGameRoleData;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

@Component
public class FlowNoticeGameRoleData implements Flow {

    @Autowired
    private MatchService matchService;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        SCFightGameRoleData.Builder builder = SCFightGameRoleData.newBuilder();
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            builder.addGameRoleData(gameRoleData);
        }

        gameBroadcast.broadcast(game, SC.newBuilder().setSCFightGameRoleData(builder).build());
    }

}
