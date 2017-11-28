package com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BetAllCallType;

@Component
public class CxBetAll extends BetAllCallType {

    @Override
    public int execute(Game game, RoleGameInfo roleGameInfo) {
        int chipMoney = roleGameInfo.chipMoney;
        roleGameInfo.betScore += roleGameInfo.chipMoney;// 赌注加上自己所有的
        roleGameInfo.betScoreRecord += roleGameInfo.chipMoney;// 赌注记录
        roleGameInfo.chipMoney = 0;// 筹码归零
        // 如果玩家的所有赌注大于目前最大赌注就进行赋值
        if (roleGameInfo.betScore > game.maxChipMoney) {
            game.maxChipMoney = roleGameInfo.betScore;
        }
        return chipMoney;
    }

}
