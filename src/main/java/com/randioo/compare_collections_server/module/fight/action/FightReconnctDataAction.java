package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightReconnectDataRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-17 9:38
 **/
@Controller
@PTAnnotation(FightReconnectDataRequest.class)
public class FightReconnctDataAction implements IActionSupport {
    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, Object session) {
        Role role = RoleCache.getRoleBySession(session);
        fightService.reconnect(role);
    }
}
