package com.randioo.compare_collections_server.module.fight.component.parser;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.ResultGameOverData;
import com.randioo.compare_collections_server.protocol.Entity.RoleGameOverInfoData;
import com.randioo.randioo_server_base.template.Parser;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Component
public class GameOverProtoParser implements Parser<ResultGameOverData, Game> {

    @Autowired
    private MatchService matchService;

    @Override
    public ResultGameOverData parse(Game game) {
        ResultGameOverData.Builder resultGameOverDataBuilder = ResultGameOverData.newBuilder()
                .setGameConfigData(game.getGameConfig())
                .setGameOverTimestamp(String.valueOf(TimeUtils.getNowTime()));

        for (Map.Entry<String, RoleGameInfo> entrySet : game.getRoleIdMap().entrySet()) {
            RoleGameInfo roleGameInfo = entrySet.getValue();
            GameRoleData gameRoleData = matchService.parseGameRoleData(roleGameInfo, game);
            GameOverResult result = game.getStatisticResultMap().get(roleGameInfo.gameRoleId);
            RoleGameOverInfoData roleGameOverInfoData = RoleGameOverInfoData.newBuilder()
                    .setChipMoney(0)
                    .setRoleId(roleGameInfo.roleId)
                    .setGameRoleData(gameRoleData)
                    .setScore(result.score)
                    .setLossCount(result.lossCount)
                    .setWinCount(result.winCount)
                    .setMaster(game.getMasterRoleId() == roleGameInfo.roleId)
                    .build();
            resultGameOverDataBuilder.addRoleGameOverInfoData(roleGameOverInfoData);
        }

        return resultGameOverDataBuilder.build();
    }
}
