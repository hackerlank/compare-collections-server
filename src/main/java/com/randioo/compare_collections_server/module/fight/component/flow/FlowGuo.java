package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.GuoCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGuo;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 通知过
 *
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowGuo implements Flow {
    @Autowired
    private GameBroadcast gameBroadcast;
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Override
    public void execute(Game game, String[] params) {
        RoleGameInfo current = roleGameInfoManager.current(game);

        GuoCallType callType = (GuoCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.GUO);
        callType.execute(game, current);
        SC scGuo = SC.newBuilder().setSCFightGuo(SCFightGuo.newBuilder().setSeat(current.seat)).build();

        gameBroadcast.broadcast(game, scGuo);
    }

}
