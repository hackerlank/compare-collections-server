package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.exit.service.ExitService;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.AudienceManager;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.GoldGameTypeManager;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.protocol.Entity.KickReason;
import com.randioo.compare_collections_server.protocol.Match.SCMatchKickGame;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.template.Session;
import com.randioo.randioo_server_base.utils.SessionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 检查金币是否足够
 *
 * @author wcy 2017年11月22日
 */
@Component
public class FlowKickByGold implements Flow {

    @Autowired
    private GoldGameTypeManager goldGameTypeManager;

    @Autowired
    private LoginService loginService;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private AudienceManager audienceManager;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private ExitService exitService;

    @Override
    public void execute(Game game, String[] params) {
        int needGold = GlobleClass._G.need_gold;
        Map<String, RoleGameInfo> roleGameInfoMap = game.getRoleIdMap();
        Map<String, KickReason> kickMap = new HashMap<>();

        //玩家
        for (RoleGameInfo roleGameInfo : roleGameInfoMap.values()) {
            Role role = loginService.getRoleById(roleGameInfo.roleId);
            String gameRoleId = roleGameInfo.gameRoleId;
            if (goldGameTypeManager.isGoldNotEnough(role, needGold)) {
                kickMap.put(gameRoleId, KickReason.GOLD_NOT_ENOUGH);
            }
            if (!online(roleGameInfo.roleId)) {
                kickMap.put(gameRoleId, KickReason.NOT_ONLINE);
            }
            if (roleGameInfo.leave) {
                kickMap.put(gameRoleId, KickReason.NOT_IN_GAME);
            }
        }
        //观众
        Map<Integer, KickReason> kickAudiencesMap = new HashMap<>();
        for (int audiecnceRoleId : audienceManager.getAudiences(game.getGameId())) {
            Role role = (Role) RoleCache.getRoleById(audiecnceRoleId);

            if (goldGameTypeManager.isGoldNotEnough(role, needGold)) {
                kickAudiencesMap.put(role.getRoleId(), KickReason.GOLD_NOT_ENOUGH);
            }
            if (!online(role.getRoleId())) {
                kickAudiencesMap.put(role.getRoleId(), KickReason.NOT_ONLINE);
            }
        }
        kick(game, kickMap, kickAudiencesMap);

        if (game.getRoleIdMap().size() == 0) {
            exitService.dismissGame(game);
        }
    }

    private boolean online(int roleId) {
        Object session = SessionCache.getSessionById(roleId);
        return session != null && Session.isConnected(session);
    }

    /**
     * 踢人
     *
     * @param game
     * @param kickRoleIdList
     * @author wcy 2017年11月22日
     */
    private void kick(Game game, Map<String, KickReason> kickMap, Map<Integer, KickReason> kickAudiencesMap) {
        game.logger.info("要移除的玩家: {}",kickMap);
        game.logger.info("要移除的观众: {}",kickAudiencesMap);
        //一定要先移除观众
        for (Map.Entry<Integer, KickReason> entry : kickAudiencesMap.entrySet()) {
            Integer roleId = entry.getKey();
//            gameBroadcast.broadcastBesides(game, SC.newBuilder()
//                    .setSCMatchExitGame(SCMatchExitGame.newBuilder().setSeat(audienceManager.getSeat(roleId, game)))
//                    .build(), roleId);

            //通知自己
            SessionUtils.sc(roleId, SC.newBuilder()
                    .setSCMatchKickGame(SCMatchKickGame.newBuilder().setReason(entry.getValue().getNumber())).build());
            //移除
            audienceManager.remove(roleId, game.getGameId());
        }


        // 从游戏列表中移除
        for (String kickGameRoleId : kickMap.keySet()) {
            RoleGameInfo roleGameInfo = gameManager.remove(game, kickGameRoleId);
//            //通知其他玩家
//            gameBroadcast.broadcastBesides(game, SC.newBuilder()
//                    .setSCMatchExitGame(SCMatchExitGame.newBuilder().setSeat(roleGameInfo.seat))
//                    .build(), roleGameInfo.roleId);
            //通知自己
            SessionUtils.sc(roleGameInfo.roleId, SC.newBuilder()
                    .setSCMatchKickGame(SCMatchKickGame.newBuilder().setReason(kickMap.get(kickGameRoleId).getNumber()))
                    .build());
        }

        game.getSeatMap().clear();
        for (int i = 0; i < game.getRoleIdList().size(); i++) {
            String gameRoleId = game.getRoleIdList().get(i);
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);
            roleGameInfo.seat = i;
            game.getSeatMap().put(i,roleGameInfo);
        }
    }
}
