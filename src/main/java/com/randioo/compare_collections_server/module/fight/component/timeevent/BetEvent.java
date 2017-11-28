package com.randioo.compare_collections_server.module.fight.component.timeevent;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-27 15:20
 **/
public class BetEvent extends DefaultTimeEvent {
    @Autowired
    private FightService fightService;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    private Role role;
    private Game game;

    public BetEvent(Role role, Game game) {
        this.role = role;
        this.game = game;
    }

    @Override
    public void update(TimeEvent timeEvent) {
        RoleGameInfo roleGameInfo = roleGameInfoManager.getByRoleId(game, role.getRoleId());
        fightService.bet(roleGameInfo, game, GlobleClass._G.sdb.default_bet_score);
    }
}
