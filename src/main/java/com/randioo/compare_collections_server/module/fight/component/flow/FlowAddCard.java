package com.randioo.compare_collections_server.module.fight.component.flow;

import java.util.Arrays;
import java.util.List;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.file.TenHalfCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.tenhalf.CardTypeGetterImpl;
import com.randioo.compare_collections_server.protocol.Fight.SCFightCardType;
import com.randioo.compare_collections_server.protocol.Fight.SCFightChooseAddCard;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.protocol.Fight.SCFightAddCard;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;

/**
 * 加牌
 * 
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowAddCard implements Flow {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameBroadcast gameBroadcast;

    @Override
    public void execute(Game game, String[] params) {
        List<Integer> remainCards = game.getRemainCards();
        String[] sendCardSeats = Arrays.copyOfRange(params, 0, params.length - 1);
        int sendCount = Integer.parseInt(params[params.length - 1]);

        for (String seatStr : sendCardSeats) {
            int seat = Integer.parseInt(seatStr);
            RoleGameInfo roleGameInfo = roleGameInfoManager.get(game, seat);
            SCFightAddCard.Builder builder = SCFightAddCard.newBuilder().setSeat(roleGameInfo.seat);
            // 添加新牌
            for (int i = 0; i < sendCount; i++) {
                Integer card = remainCards.remove(0);
                roleGameInfo.cards.add(card);
                builder.addCard(card);
            }
            gameBroadcast.broadcast(game, SC.newBuilder().setSCFightAddCard(builder).build());
            // 通知
            game.logger.info("roleGameInfo:{} cards:{}", roleGameInfo.gameRoleId, roleGameInfo.cards);

        }

    }

    /**
     * @author zsy
     * @Description: 通知选择补牌
     * @date 2017年9月28日 上午10:12:36
     */
    @Component
    public static class FlowNoticeChooseAddCard implements Flow {
        @Autowired
        private RoleGameInfoManager roleGameInfoManager;

        @Autowired
        private GameBroadcast gameBroadcast;

        @Autowired
        private EventBus eventBus;

        @Override
        public void execute(Game game, String[] params) {

            RoleGameInfo roleGameInfo = roleGameInfoManager.current(game);

            game.callTypeList.clear();
            game.callTypeList.add(CallTypeEnum.CHOOSE_ADD_CARD);

            gameBroadcast.broadcast(game, SC.newBuilder()
                    .setSCFightChooseAddCard(SCFightChooseAddCard.newBuilder().setSeat(roleGameInfo.seat)).build());

        }

    }

    /**
     * @author zsy
     * @Description: 爆牌或十点时通知所有人
     * @create 2017-11-08 10:17
     **/
    @Component
    public static class FlowNoticeCardType implements Flow {
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
            SCFightCardType.Builder scFightCardType = SCFightCardType.newBuilder()
                    .setSeat(roleGameInfo.seat);

            scFightCardType.setCardType(26);

            //要显示的牌型
    //        if (FightConstant.SHOW_CARD_TYPE.contains(cardType.id)) {
    //            scFightCardType.setCardType(cardType.id);
    //        }

            broadcast.broadcast(game, SC.newBuilder()
                    .setSCFightCardType(scFightCardType)
                    .build());
        }
    }
}
