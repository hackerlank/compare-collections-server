package com.randioo.compare_collections_server.module.fight.component.timeevent;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * 弃牌事件
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
public class GiveUpEvent extends AbstractCompareTimeEvent {

    public GiveUpEvent(Game game, String gameRoleId, int verifyId) {
        super(game, gameRoleId, verifyId);
    }

    @Override
    public void execute(TimeEvent timeEvent, RoleGameInfo roleGameInfo) {
        FightService fightService = SpringContext.getBean(FightService.class);
        fightService.coreGiveUp(game, gameRoleId);
    }
}
