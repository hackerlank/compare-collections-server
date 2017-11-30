package com.randioo.compare_collections_server.module.fight.component.rule;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

/**
 * 安全检查接口
 * 
 * @author wcy 2017年11月10日
 *
 */
public interface ISafeCheckCallType {
    public boolean checkFollow(RoleGameInfo roleGameInfo, Game game);

    public boolean checkBigger(RoleGameInfo roleGameInfo, Game game, int biggerMoney);

    public boolean checkGuo(RoleGameInfo roleGameInfo, Game game);

}
