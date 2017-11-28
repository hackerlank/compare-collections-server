package com.randioo.compare_collections_server.module.exit.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.exit.service.ExitService;
import com.randioo.compare_collections_server.protocol.Fight.FightExitGameRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 *
 */
@Controller
@PTAnnotation(FightExitGameRequest.class)
public class ExitGameAction implements IActionSupport {

	@Autowired
	private ExitService exitService;

	@Override
	public void execute(Object data, Object session) {
		Role role = RoleCache.getRoleBySession(session);
        exitService.exitGame(role);
	}
}
