package com.randioo.compare_collections_server.module.match.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Match.MatchRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-21 17:21
 **/
@Controller
@PTAnnotation(MatchRequest.class)
public class MatchAction implements IActionSupport {
    @Autowired
    private MatchService matchService;

    @Override
    public void execute(Object data, Object session) {
        Role role = RoleCache.getRoleBySession(session);
        MatchRequest request  = (MatchRequest) data;
        matchService.match(role,request.getMatchParameter());
    }
}
