package com.randioo.compare_collections_server.module.fight.component.rule.zjh;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.SafeCheckCallTypeAdapter;

@Component
public class ZJHSafeCheckCallType extends SafeCheckCallTypeAdapter {
	@Override
	public boolean checkFollow(RoleGameInfo roleGameInfo, Game game) {
		return false;
	}

	@Override
	public boolean checkBigger(RoleGameInfo roleGameInfo, Game game, int bigMoney) {
		return game.maxChipMoney > bigMoney;
	}
}
