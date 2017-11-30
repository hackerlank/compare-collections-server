/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.parser.CountdownProtoParser;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.timeevent.BetEvent;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Fight;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.EventScheduler;

/**
 * 通知押注
 *
 * @author zsy
 * @Description:
 * @date 2017年9月22日 上午10:37:34
 */
@Component
public class FlowNoticeBet implements Flow {
    @Autowired
    private EventBus eventBus;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private CountdownProtoParser countdownProtoParser;

    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private EventScheduler eventScheduler;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private VerifyManager verifyManager;

    @Override
    public void execute(Game game, String[] params) {
        int seat = game.getCurrentSeat();
        game.callTypeList.clear();
        game.callTypeList.add(CallTypeEnum.BET);

        RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);

        verifyManager.reset(roleGameInfo.verify);

        Fight.SCFightNoticeBet scFightNoticeBet = Fight.SCFightNoticeBet.newBuilder().setSeat(seat).build();
        SC sc = SC.newBuilder().setSCFightNoticeBet(scFightNoticeBet).build();

        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            eventScheduler.addEvent(new BetEvent(game, roleGameInfo.gameRoleId, roleGameInfo.verify.verifyId));
            gameManager.recordCountdown(game);
            gameBroadcast.broadcast(game, countdownProtoParser.parse(GlobleClass._G.wait_time));
        }
        gameBroadcast.broadcast(game, sc);
    }

}
