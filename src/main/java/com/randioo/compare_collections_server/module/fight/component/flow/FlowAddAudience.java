package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FlowAddAudience implements Flow {

    @Autowired
    private AudienceManager audienceManager;

    @Autowired
    private MatchService matchService;

    @Override
    public void execute(Game game, String[] params) {
        int extractCount = Integer.parseInt(params[0]);
        List<Integer> list = audienceManager.extractCount(game.getGameId(), extractCount);
        for (int roleId : list) {
            matchService.addAccountRole(game, roleId);
        }
        game.logger.info("===========================观众加入后==============================");
        game.logger.info("RoleIdMap的情况");
        game.logger.info(game.getRoleIdMap().toString());
    }
}
