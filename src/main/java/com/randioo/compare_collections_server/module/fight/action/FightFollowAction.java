package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightGenRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * 玩家跟牌控制入口
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Controller
@PTAnnotation(FightGenRequest.class) // 跟牌请求
public class FightFollowAction implements IActionSupport {
	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, Object session) {
		FightGenRequest request = FightGenRequest.newBuilder().build();
		int money = request.getStall();
		Role role = RoleCache.getRoleBySession(session);
		fightService.follow(role, money);
	}
}
