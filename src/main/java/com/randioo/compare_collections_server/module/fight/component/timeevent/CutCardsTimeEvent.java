package com.randioo.compare_collections_server.module.fight.component.timeevent;

import java.util.ArrayList;
import java.util.List;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.utils.SpringContext;

public class CutCardsTimeEvent extends AbstractCompareTimeEvent {

    public CutCardsTimeEvent(Game game, String gameRoleId, int verifyId) {
        super(game, gameRoleId, verifyId);
    }

    @Override
    public void execute(TimeEvent timeEvent, RoleGameInfo roleGameInfo) {
        FightService fightService = SpringContext.getBean(FightService.class);
        List<Integer> cards = new ArrayList<>();
        cards.addAll(roleGameInfo.cards);
        fightService.coreCutCards(game, gameRoleId, cards);
    }
}
