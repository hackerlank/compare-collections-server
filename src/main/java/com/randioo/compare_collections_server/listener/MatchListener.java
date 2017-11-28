package com.randioo.compare_collections_server.listener;

import com.google.common.eventbus.Subscribe;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.event.MatchSucessEvent;
import com.randioo.compare_collections_server.module.fight.component.parser.GameConfigProtoParser;
import com.randioo.compare_collections_server.module.match.component.MatchInfo;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.eventbus.Listener;
import com.randioo.randioo_server_base.service.Async;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-22 11:40
 **/
@Async
@Component
public class MatchListener implements Listener {
    @Autowired
    private MatchService matchService;

    @Autowired
    private GameConfigProtoParser gameConfigProtoParser;

    @Subscribe
    public void matchSucess(MatchSucessEvent event) {
        if (event.game != null) {
            synchronized (event.game) {
                joinGame(event.game, event.MatchInfoList.get(0));
            }
        } else {
            createGame(event.MatchInfoList);
        }

    }

    private void createGame(List<MatchInfo> matchInfoList) {
        int matchParameter = matchInfoList.get(0).matchParameter;
        GameConfigData gameConfigData = gameConfigProtoParser.parse(matchParameter);

        Game game = matchService.createGameByGameConfig(gameConfigData, GameType.GAME_TYPE_GOLD);
        game.setGameType(GameType.GAME_TYPE_GOLD);
        game.matchParameter = matchParameter;
        for (MatchInfo matchInfo : matchInfoList) {
            Role role = RoleCache.getRoleById(matchInfo.roleId);
            matchService.matchJoinGame(role, game.getGameConfig().getRoomId());
        }
        game.logger.info("开了一个房, 房间号: {}", game.getGameId());
    }

    private void joinGame(Game game, MatchInfo matchInfo) {
        Role role = RoleCache.getRoleById(matchInfo.roleId);
        if (game.getGameState() == GameState.GAME_STATE_START) {
            matchService.audienceJoinGame(role, game.getGameConfig().getRoomId());
        } else {
            matchService.matchJoinGame(role, game.getGameConfig().getRoomId());
        }
    }
}
