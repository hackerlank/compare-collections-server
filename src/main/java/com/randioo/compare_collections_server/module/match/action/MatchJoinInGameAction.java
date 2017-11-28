package com.randioo.compare_collections_server.module.match.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Match.MatchJoinInGameRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 *
 */
@Controller
@PTAnnotation(MatchJoinInGameRequest.class)
public class MatchJoinInGameAction implements IActionSupport {
	@Autowired
	private MatchService matchService;

	@Override
	public void execute(Object data, Object session) {
		MatchJoinInGameRequest request = (MatchJoinInGameRequest) data;
		Role role = RoleCache.getRoleBySession(session);
		String roomId = request.getRoomId();
		matchService.joinInRoom(role, roomId);
	}
}
