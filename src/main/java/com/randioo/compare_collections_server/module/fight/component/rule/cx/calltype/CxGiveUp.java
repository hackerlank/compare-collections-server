package com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.GiveUpCallType;

@Component
public class CxGiveUp extends GiveUpCallType {

    @Override
    public void execute(Game game, RoleGameInfo roleGameInfo) {
        game.betPool += roleGameInfo.betScore;
        roleGameInfo.betScore = 0;
    }

}
