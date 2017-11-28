package com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.FollowCallType;

@Component
public class CxFollow extends FollowCallType {

    @Override
    public int execute(Game game, RoleGameInfo roleGameInfo) {
        int followAddChip = game.maxChipMoney - roleGameInfo.betScore;
        roleGameInfo.betScore += followAddChip;
        roleGameInfo.betScoreRecord += followAddChip;// 赌注记录
        roleGameInfo.chipMoney -= followAddChip;

        return followAddChip;
    }

}
