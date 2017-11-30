package com.randioo.compare_collections_server.module.close.service;

import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.exit.service.ExitService;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.match.component.MatchSystem;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.randioo.compare_collections_server.dao.RoleDAO;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.randioo_server_base.annotation.BaseServiceAnnotation;
import com.randioo.randioo_server_base.db.GameDB;
import com.randioo.randioo_server_base.service.BaseService;
import com.randioo.randioo_server_base.template.EntityRunnable;
import com.randioo.randioo_server_base.utils.SaveUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@BaseServiceAnnotation("closeService")
@Service("closeService")
public class CloseServiceImpl extends BaseService implements CloseService {

    @Autowired
    private LoginService loginService;

    @Autowired
    private FightService fightService;

    @Autowired
    private GameDB gameDB;

    @Autowired
    private RoleDAO roleDAO;

    @Autowired
    private MatchService matchService;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private ExitService exitService;

    @Override
    public void beforeCloseHandle(Role role) {
        //进入托管状态
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            return;
        }
        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
            if (roleGameInfo != null) {
                //只有一个人，解散这个游戏
                if (gameManager.roleCount(game) == 1 && game.getRoleIdMap().containsValue(roleGameInfo)) {
                    exitService.dismissGame(game);
                }
            }
        }
    }

    @Override
    public void asynManipulate(Role role) {
        if (role == null) return;

        role.logger.info("保存至数据库");

        role.setOfflineTimeStr(TimeUtils.getDetailTimeStr());

        matchService.serviceCancelMatch(role);
        beforeCloseHandle(role);

        // TODO
        // fightService.disconnect(role);

        if (!gameDB.isUpdatePoolClose()) {
            gameDB.getUpdatePool().submit(new EntityRunnable<Role>(role) {
                @Override
                public void run(Role role) {
                    roleDataCache2DB(role, true);
                }
            });
        }
    }

    @Override
    public void roleDataCache2DB(Role role, boolean mustSave) {
        try {
            if (SaveUtils.needSave(role, mustSave)) {
                roleDAO.update(role);
                role.logger.info("数据库表 << role >> 保存成功");
            }
        } catch (Exception e) {
            role.logger.error("数据保存出错", e);
        }
    }
}
