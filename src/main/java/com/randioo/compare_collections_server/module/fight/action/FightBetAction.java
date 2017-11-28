/**
 *
 */
package com.randioo.compare_collections_server.module.fight.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.fight.service.FightService;
import com.randioo.compare_collections_server.protocol.Fight;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;

/**
 * @author zsy
 * @Description:
 * @date 2017年9月22日 上午11:16:08
 */
@PTAnnotation(Fight.FightBetRequest.class)
@Controller
public class FightBetAction implements IActionSupport {
    @Autowired
    private FightService fightService;

    @Override
    public void execute(Object data, Object session) {
        Role role = RoleCache.getRoleBySession(session);
        Fight.FightBetRequest requset = (Fight.FightBetRequest) data;
        fightService.bet(requset.getScore(), role);
    }
}
