package com.randioo.compare_collections_server.module.fight.component.manager;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.bo.Role;

/**
 * 金币场管理器
 * 
 * @author wcy 2017年11月22日
 *
 */
@Component
public class GoldGameTypeManager {
    /**
     * 金币是否足够
     * 
     * @param role
     * @param gold
     * @return
     * @author wcy 2017年11月22日
     */
    public boolean isGoldNotEnough(Role role, int gold) {
        return role.getGold() < gold;
    }
}
