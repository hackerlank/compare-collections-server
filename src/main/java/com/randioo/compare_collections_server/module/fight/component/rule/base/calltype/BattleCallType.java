package com.randioo.compare_collections_server.module.fight.component.rule.base.calltype;

import java.util.List;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;

/**
 * 对决
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
public abstract class BattleCallType implements CallType {
	public abstract int execute(Game game, List<RoleGameInfo> list);
}
