package com.randioo.compare_collections_server.module.fight.component.rule.zjh.calltype;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.GiveUpCallType;

/**
 * give up
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZjhGiveUp extends GiveUpCallType {
	@Override
	public void execute(Game game, RoleGameInfo roleGameInfo) {
		game.betPool += roleGameInfo.betScore;
		roleGameInfo.betScore = 0;
	}
}
