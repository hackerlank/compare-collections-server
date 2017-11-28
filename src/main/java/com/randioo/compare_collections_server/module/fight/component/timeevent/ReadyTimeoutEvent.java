package com.randioo.compare_collections_server.module.fight.component.timeevent;

import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.exit.service.ExitService;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * @author zsy
 * @Description: 一直不准备自动离开游戏
 * @create 2017-11-20 9:47
 **/
public class ReadyTimeoutEvent extends DefaultTimeEvent {
    private Role role;

    public void setRole(Role role) {
        this.role = role;
    }

    @Override
    public void update(TimeEvent timeEvent) {
        Game game = GameCache.getGameMap().get(role.getGameId());
        if (game == null) {
            return;
        }
        ExitService exitService = SpringContext.getBean(ExitService.class);
        RoleGameInfoManager roleGameInfoManager = SpringContext.getBean(RoleGameInfoManager.class);

        RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
        //已经准备了直接返回
        if (roleGameInfo.ready) {
            return;
        }
      //  exitService.exitGame2(game, role);
    }
}
