package com.randioo.compare_collections_server.module.fight.component.rule;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

public class SafeCheckCallTypeAdapter implements ISafeCheckCallType {

    @Override
    public boolean checkFollow(RoleGameInfo roleGameInfo, Game game) {
        return false;
    }

    @Override
    public boolean checkBigger(RoleGameInfo roleGameInfo, Game game, int biggerMoney) {
        return false;
    }

    @Override
    public boolean checkGuo(RoleGameInfo roleGameInfo, Game game) {
        return false;
    }

}
