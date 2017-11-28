package com.randioo.compare_collections_server.module.fight.component.rule.base.calltype;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;

public abstract class BetAllCallType implements CallType {

    /**
     * 返回下注数
     * 
     * @param game
     * @return 返回值是此人剩余的所有赌注
     * @author wcy 2017年11月6日
     */
    public abstract int execute(Game game, RoleGameInfo roleGameInfo);

}
