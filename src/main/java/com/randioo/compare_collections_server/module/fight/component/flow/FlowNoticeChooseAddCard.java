package com.randioo.compare_collections_server.module.fight.component.flow;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.protocol.Fight.SCFightChooseAddCard;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.compare_collections_server.quartz.QuartzManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

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
    private QuartzManager quartzManager;

    @Override
    public void execute(Game game, String[] params) {

        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

        game.callTypeList.clear();
        game.callTypeList.add(CallTypeEnum.CHOOSE_ADD_CARD);

        gameBroadcast.broadcast(game, SC.newBuilder()
                .setSCFightChooseAddCard(SCFightChooseAddCard.newBuilder().setSeat(roleGameInfo.seat)).build());

        // if (game.getGameType() == GameType.GAME_TYPE_GOLD) {
        Map<String, Object> map = new HashMap<>();
        map.put("game", game);
        map.put("roleGameInfo", roleGameInfo);
        map.put("seat", roleGameInfo.seat);
      //  quartzManager.addJob(ChooseAddCardJob.class, GlobleClass._G.wait_time, roleGameInfo.gameRoleId, map);
        //     }

    }

}