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
 * @create 2017-11-28 11:32
 **/
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
public class ChooseAddCardJob implements Job {
    private Game game;
    private RoleGameInfo roleGameInfo;

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        FightService fightService = SpringContext.getBean(FightService.class);
        fightService.coreContinueAddCard(game, roleGameInfo, GlobleClass._G.sdb.defalut_choose_card);
    }

    public void setRoleGameInfo(RoleGameInfo roleGameInfo) {
        this.roleGameInfo = roleGameInfo;
    }

    public void setGame(Game game) {
        this.game = game;
    }
}
