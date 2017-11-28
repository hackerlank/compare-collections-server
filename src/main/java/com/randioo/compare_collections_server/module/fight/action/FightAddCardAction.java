/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight.FightChooseAddCardRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月28日 上午10:53:18
 */
@PTAnnotation(FightChooseAddCardRequest.class)
@Controller
public class FightAddCardAction implements IActionSupport {
    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, Object session) {
        FightChooseAddCardRequest request = (FightChooseAddCardRequest) data;
        boolean isAddCard = request.getIsAddCard();
        Role role = RoleCache.getRoleBySession(session);
        fightService.continueAddCard(role, isAddCard);
    }
}
