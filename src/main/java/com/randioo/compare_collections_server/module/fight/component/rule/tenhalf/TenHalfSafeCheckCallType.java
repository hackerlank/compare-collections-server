package com.randioo.compare_collections_server.module.fight.component.rule.tenhalf;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.rule.SafeCheckCallTypeAdapter;

@Component
public class TenHalfSafeCheckCallType extends SafeCheckCallTypeAdapter {

    @Autowired
    private VerifyManager verifyManager;

    public boolean iCallSafe(Game game, RoleGameInfo roleGameInfo) {
        boolean safe = verifyManager.checkVerify(roleGameInfo.verify);
        if (!safe) {
            game.logger.info("安全监测：出现错误 seat {}, rolId {}", roleGameInfo.seat, roleGameInfo.roleId);
        }
        return safe;
    }
}
