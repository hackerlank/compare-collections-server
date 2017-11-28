/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightReadyRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月21日 下午3:08:27
 */
@Controller
@PTAnnotation(FightReadyRequest.class)
public class FightReadyAction implements IActionSupport {
	@Autowired
	FightService fightService;

	@Override
	public void execute(Object data, Object session) {
		Role role = RoleCache.getRoleBySession(session);
		fightService.ready(role);
	}
}
