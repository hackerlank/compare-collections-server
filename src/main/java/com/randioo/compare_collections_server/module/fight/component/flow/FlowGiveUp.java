package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.GiveUpCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightGiveUp;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 弃牌
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowGiveUp implements Flow {
    @Autowired
    private GameBroadcast gameBroadcast;
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Override
    public void execute(Game game, String[] params) {
        RoleGameInfo current = roleGameInfoManager.current(game);
        
        GiveUpCallType callType = (GiveUpCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.GIVE_UP);
        callType.execute(game, current);
        SC giveUp = SC.newBuilder().setSCFightGiveUp(SCFightGiveUp.newBuilder().setSeat(current.seat)).build();

        // 通知所有人
        gameBroadcast.broadcast(game, giveUp);
    }
}
