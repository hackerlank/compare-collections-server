package com.randioo.compare_collections_server.module.match.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Match.MatchCreateGameRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(MatchCreateGameRequest.class)
public class MatchCreateGameAction implements IActionSupport {

    @Autowired
    private MatchService matchService;

    @Override
    public void execute(Object data, Object session) {
        MatchCreateGameRequest request = (MatchCreateGameRequest) data;
        Role role =  RoleCache.getRoleBySession(session);
        matchService.createRoom(role, request.getGameConfigData());
    }

}
