package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightGiveUpRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * 玩家弃牌控制入口
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Controller
@PTAnnotation(FightGiveUpRequest.class) // 弃牌请求
public class FightGiveUpAction implements IActionSupport {
	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, Object session) {
		Role role = RoleCache.getRoleBySession(session);
		fightService.giveUp(role);
	}
}
