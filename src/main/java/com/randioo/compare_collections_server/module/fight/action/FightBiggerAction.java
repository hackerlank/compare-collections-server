package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightBiggerRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * 玩家加注控制入口
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Controller
@PTAnnotation(FightBiggerRequest.class) // 跟注请求
public class FightBiggerAction implements IActionSupport {
	@Autowired
	private FightService fightService;

	@Override
	public void execute(Object data, Object session) {
		FightBiggerRequest request = (FightBiggerRequest) data;
		Role role = RoleCache.getRoleBySession(session);
		fightService.bigger(role, request.getStall());
	}
}
