package com.randioo.compare_collections_server.module.fight.component.rule.base.calltype;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;

public abstract class BiggerCallType implements CallType {

    /**
     * 
     * @param game
     * @param roleGameInfo
     * @param bigMoney
     * @return 目前最大赌值+大的注数-本人目前的已下的注数
     * @author wcy 2017年11月6日
     */
    public abstract int exeute(Game game, RoleGameInfo roleGameInfo, int bigMoney);

}
