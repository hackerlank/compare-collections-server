package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype.BetAllCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightBetAll;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 敲
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowBetAll implements Flow {
    @Autowired
    private GameBroadcast gameBroadcast;
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Override
    public void execute(Game game, String[] params) {
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

        BetAllCallType callType = (BetAllCallType) game.getRule().getCallTypeByEnum(CallTypeEnum.BET_ALL);
        int chipMoney = callType.execute(game, roleGameInfo);
        SC scBigerBet = SC.newBuilder()
                .setSCFightBetAll(SCFightBetAll.newBuilder().setSeat(roleGameInfo.seat).setBets(chipMoney))
                .build();

        // 通知其他人
        gameBroadcast.broadcast(game, scBigerBet);
    }
}
