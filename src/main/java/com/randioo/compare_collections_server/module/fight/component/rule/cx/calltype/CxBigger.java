package com.randioo.compare_collections_server.module.fight.component.rule.cx.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BiggerCallType;

@Component
public class CxBigger extends BiggerCallType {

    @Override
    public int exeute(Game game, RoleGameInfo current, int bigMoney) {
        // 大的意思是比目前最大值还要大
        int finallyBet = game.maxChipMoney + bigMoney;
        int needBet = finallyBet - current.betScore;

        current.chipMoney -= needBet;
        current.betScore += needBet;
        current.betScoreRecord += needBet;// 赌注记录
        game.maxChipMoney = current.betScore;

        return needBet;
    }

}
