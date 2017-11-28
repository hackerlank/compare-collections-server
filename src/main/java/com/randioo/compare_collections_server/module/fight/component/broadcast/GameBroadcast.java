package com.randioo.compare_collections_server.module.fight.component.broadcast;

import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.randioo_server_base.utils.SessionUtils;

/**
 * 房间广播器
 *
 * @author wcy 2017年9月17日
 */
@Component
public class GameBroadcast implements Broadcast<Game, GeneratedMessage> {
    @Autowired
    private AudienceManager audienceManager;

    @Override
    public void broadcast(Game entity, GeneratedMessage message) {
        for (RoleGameInfo roleGameInfo : entity.getRoleIdMap().values()) {
            SessionUtils.sc(roleGameInfo.roleId, message);
        }
        broadWait(entity, message);
    }

    /**
     * 除了roleId这个人不通知
     *
     * @param entity
     * @param message
     * @param roleId
     */
    @Override
    public void broadcastBesides(Game entity, GeneratedMessage message, int roleId) {
        for (RoleGameInfo roleGameInfo : entity.getRoleIdMap().values()) {
            if (roleGameInfo.roleId == roleId) {
                continue;
            }
            SessionUtils.sc(roleGameInfo.roleId, message);
        }
        broadWait(entity, message, roleId);
    }

    /**
     * 通知观战的玩家
     *
     * @param game
     * @param message
     */
    private void broadWait(Game game, GeneratedMessage message) {
        for (int roleId : audienceManager.getAudiences(game.getGameId())) {
            SessionUtils.sc(roleId, message);
        }
    }

    private void broadWait(Game game, GeneratedMessage message, int roleId) {
        for (int audienceId : audienceManager.getAudiences(game.getGameId())) {
            if (audienceId == roleId) {
                System.out.println("跳过的人roleId  "+roleId);
                continue;
            }
            SessionUtils.sc(audienceId, message);
        }
    }

}
