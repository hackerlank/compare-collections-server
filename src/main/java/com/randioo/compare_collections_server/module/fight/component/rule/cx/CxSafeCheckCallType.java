package com.randioo.compare_collections_server.module.fight.component.rule.cx;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.SafeCheckCallTypeAdapter;

@Component
public class CxSafeCheckCallType extends SafeCheckCallTypeAdapter {

    @Override
    public boolean checkFollow(RoleGameInfo roleGameInfo, Game game) {
        return roleGameInfo.chipMoney + roleGameInfo.betScore <= game.maxChipMoney;
    }

    @Override
    public boolean checkBigger(RoleGameInfo roleGameInfo, Game game, int bigMoney) {
        return roleGameInfo.chipMoney + roleGameInfo.betScore <= game.maxChipMoney + bigMoney;
    }

    @Override
    public boolean checkGuo(RoleGameInfo roleGameInfo, Game game) {
        return game.maxChipMoney > 0;
    }

}
