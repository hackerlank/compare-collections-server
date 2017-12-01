package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeCallType;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.parser.CountdownProtoParser;
import com.randioo.compare_collections_server.module.fight.component.timeevent.GiveUpEvent;
import com.randioo.compare_collections_server.protocol.Fight.SCFightCountdown;
import com.randioo.compare_collections_server.protocol.Fight.SCFightNoticeCallType;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

@Component
public class FlowNoticeCxCallType implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private CountdownProtoParser countdownProtoParser;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private VerifyManager verifyManager;

    @Override
    public void execute(Game game, String[] params) {
        List<Integer> type = new ArrayList<>();
        for (int i = 0; i < game.callTypeList.size(); i++) {
            type.add(game.callTypeList.get(i).getValue());
        }
        RoleGameInfo current = roleGameInfoManager.current(game);

        verifyManager.reset(current.verify);

        SC sc = SC.newBuilder()
                .setSCFightNoticeCallType(SCFightNoticeCallType.newBuilder().addAllTypes(type).setSeat(current.seat))
                .build();
        SessionUtils.sc(current.roleId, sc);

        eventBus.post(new EventNoticeCallType(game, sc, current.gameRoleId));

        if (gameManager.isGoldMode(game)) {

            GiveUpEvent event = new GiveUpEvent(game, current.gameRoleId, current.verify.verifyId);
            event.setEndTime(TimeUtils.getNowTime() + 8);
//            eventScheduler.addEvent(event);

            gameManager.recordCountdown(game);
            gameBroadcast.broadcast(
                    game,
                    SC.newBuilder()
                            .setSCFightCountdown(
                                    SCFightCountdown.newBuilder()
                                            .setCountdown(GlobleClass._G.wait_time)
                                            .setSeat(current.seat))
                            .build());
        }
    }
}
