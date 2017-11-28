package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeCallType;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightNoticeCallType;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Component
public class FlowNoticeCxCallType implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private EventBus eventBus;

    @Override
    public void execute(Game game, String[] params) {
        List<Integer> type = new ArrayList<>();
        for (int i = 0; i < game.callTypeList.size(); i++) {
            type.add(game.callTypeList.get(i).getValue());
        }
        RoleGameInfo current = roleGameInfoManager.current(game);
        game.actionVerifyId++;
        current.actionVerifyId = game.actionVerifyId;
        SC sc = SC.newBuilder()
                .setSCFightNoticeCallType(SCFightNoticeCallType.newBuilder().addAllTypes(type).setSeat(current.seat))
                .build();
        SessionUtils.sc(current.roleId, sc);

        eventBus.post(new EventNoticeCallType(game, sc, current.gameRoleId));
    }
}
