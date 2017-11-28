package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightGetRoomDataRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

@Controller
@PTAnnotation(FightGetRoomDataRequest.class)
public class FightSerializeGameAction implements IActionSupport {

    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, Object session) {
        Role role = RoleCache.getRoleBySession(session);
        fightService.getGameSerialize(role);
    }

}
