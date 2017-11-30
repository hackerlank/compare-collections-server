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
import com.randioo.compare_collections_server.module.fight.component.timeevent.ChooseAddCardEvent;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Fight.SCFightChooseAddCard;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.scheduler.EventScheduler;

/**
 * @author zsy
 * @Description: 通知选择补牌
 * @date 2017年9月28日 上午10:12:36
 */
@Component
public class FlowNoticeChooseAddCard implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Autowired
    private EventBus eventBus;

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

        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

        game.callTypeList.clear();
        game.callTypeList.add(CallTypeEnum.CHOOSE_ADD_CARD);

        verifyManager.reset(roleGameInfo.verify);

        if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
            eventScheduler.addEvent(new ChooseAddCardEvent(game, roleGameInfo.gameRoleId, roleGameInfo.verify.verifyId));
            gameManager.recordCountdown(game);
            gameBroadcast.broadcast(game, countdownProtoParser.parse(GlobleClass._G.wait_time));
        }
        gameBroadcast.broadcast(game,
                SC.newBuilder()
                        .setSCFightChooseAddCard(SCFightChooseAddCard.newBuilder().setSeat(roleGameInfo.seat))
                        .build());
    }

}