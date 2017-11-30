package com.randioo.compare_collections_server.module.fight.component.timeevent;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.utils.SpringContext;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-27 15:20
 **/
public class BetEvent extends AbstractCompareTimeEvent {

    public BetEvent(Game game, String gameRoleId, int verifyId) {
        super(game, gameRoleId, verifyId);
        setEndTime(GlobleClass._G.wait_time + TimeUtils.getNowTime());
    }

    @Override
    public void execute(TimeEvent timeEvent, RoleGameInfo roleGameInfo) {
        FightService fightService = SpringContext.getBean(FightService.class);
        fightService.bet(roleGameInfo, game, GlobleClass._G.sdb.default_bet_score);
    }
}
