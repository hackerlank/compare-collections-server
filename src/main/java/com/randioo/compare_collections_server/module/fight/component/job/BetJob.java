package com.randioo.compare_collections_server.module.fight.component.job;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.utils.SpringContext;
import org.quartz.*;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-28 9:34
 **/
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class BetJob implements Job {
    private Game game;
    private RoleGameInfo roleGameInfo;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        FightService fightService = SpringContext.getBean(FightService.class);

        fightService.bet(roleGameInfo, game, GlobleClass._G.sdb.default_bet_score);
    }

    public void setRoleGameInfo(RoleGameInfo roleGameInfo) {
        this.roleGameInfo = roleGameInfo;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
