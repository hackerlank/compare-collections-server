package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.parser.CountdownProtoParser;
import com.randioo.compare_collections_server.module.fight.component.timeevent.GiveUpEvent;
import com.randioo.compare_collections_server.protocol.Fight.SCFightNoticeCallType;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.EventScheduler;
import com.randioo.randioo_server_base.utils.SessionUtils;
import com.randioo.randioo_server_base.utils.TimeUtils;

/**
 * 主推玩家可以操作的按钮
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowNoticeZjhCallType implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;
    @Autowired
    private CountdownProtoParser countdownProtoParser;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private GameManager gameManager;

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

        if (gameManager.isGoldMode(game)) {
            GiveUpEvent event = new GiveUpEvent(game, current.gameRoleId, current.verify.verifyId);
            event.setEndTime(TimeUtils.getNowTime() + 15);
            eventScheduler.addEvent(event);

            SessionUtils.sc(current.roleId, countdownProtoParser.parse(GlobleClass._G.wait_time));
        }
        SC sc = SC.newBuilder()
                .setSCFightNoticeCallType(SCFightNoticeCallType.newBuilder().addAllTypes(type).setSeat(current.seat))
                .build();
        SessionUtils.sc(current.roleId, sc);
    }
}
