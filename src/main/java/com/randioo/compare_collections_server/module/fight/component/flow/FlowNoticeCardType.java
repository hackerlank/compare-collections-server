package com.randioo.compare_collections_server.module.fight.component.flow;

import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardTypeGetterImpl;
import com.randioo.compare_collections_server.protocol.Fight.SCFightCardType;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @author zsy
 * @Description: 爆牌或十点时通知所有人
 * @create 2017-11-08 10:17
 **/
@Component
public class FlowNoticeCardType implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private CardTypeGetterImpl cardTypeGetter;

    @Autowired
    private GameBroadcast broadcast;

    @Override
    public void execute(Game game, String[] params) {
        RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);
        TenHalfCardTypeConfig cardType = cardTypeGetter.get(roleGameInfo.cards);
        SCFightCardType.Builder scFightCardType = SCFightCardType.newBuilder().setSeat(roleGameInfo.seat);

        scFightCardType.setCardType(26);

        //要显示的牌型
        //        if (FightConstant.SHOW_CARD_TYPE.contains(cardType.id)) {
        //            scFightCardType.setCardType(cardType.id);
        //        }

        broadcast.broadcast(game, SC.newBuilder().setSCFightCardType(scFightCardType).build());
    }
}
