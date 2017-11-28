package com.randioo.compare_collections_server.module.exit.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.exit.service.ExitService;
import com.randioo.compare_collections_server.protocol.Fight.FightApplyExitGameRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightApplyExitGameRequest.class)
public class ExitApplyExitGameAction implements IActionSupport {

    @Autowired
    private ExitService exitService;

    @Override
    public void execute(Object data, Object session) {
        FightApplyExitGameRequest request = (FightApplyExitGameRequest) data;
        Role role = RoleCache.getRoleBySession(session);
        exitService.applyExitGame(role);
    }

}
