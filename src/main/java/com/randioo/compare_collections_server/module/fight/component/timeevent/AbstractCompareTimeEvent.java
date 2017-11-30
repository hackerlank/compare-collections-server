package com.randioo.compare_collections_server.module.fight.component.timeevent;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;

public abstract class AbstractCompareTimeEvent extends DefaultTimeEvent {

    public Game game;
    public String gameRoleId;
    public int verifyId;

    public AbstractCompareTimeEvent(Game game, String gameRoleId, int verifyId) {
        this.game = game;
        this.gameRoleId = gameRoleId;
        this.verifyId = verifyId;
    }

    @Override
    public void update(TimeEvent timeEvent) {
        synchronized (game) {
            RoleGameInfo roleGameInfo = game.getRoleIdMap().get(gameRoleId);

            if (roleGameInfo == null || roleGameInfo.verify.useId != verifyId) {
                return;
            }

            execute(timeEvent, roleGameInfo);
        }

    }

    public abstract void execute(TimeEvent timeEvent, RoleGameInfo roleGameInfo);

}
