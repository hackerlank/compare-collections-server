package com.randioo.compare_collections_server.module.fight.component.rule.base.calltype;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;

/**
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 *
 */
public abstract class WatchCallType implements CallType {
	public abstract void execute(Game game, RoleGameInfo roleGameInfo);
}
