/**
 *
 */
package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.EventBus;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.broadcast.GameBroadcast;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeRoundOver;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.manager.VerifyManager;
import com.randioo.compare_collections_server.module.fight.component.parser.GameOverProtoParser;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import com.randioo.compare_collections_server.protocol.Entity.CardData;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightRoundOver;
import com.randioo.compare_collections_server.protocol.Fight.SCFightRoundOver.Builder;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.cache.RoleCache;

/**
 * @author zsy
 * @Description:
 * @date 2017年10月25日 上午10:40:54
 */
@Component
public class FlowRoundOver_Gold implements Flow {

    @Autowired
    RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private MatchService matchService;

    @Autowired
    GameBroadcast gameBroadcast;

    @Autowired
    private EventBus eventBus;

    @Autowired
    private GameOverProtoParser gameOverProtoParser;

    @Autowired
    private GameManager gameManager;

    @Autowired
    private VerifyManager verifyManager;

    @Override
    public void execute(Game game, String[] params) {

        // 所有人校验器设置过期
        for (RoleGameInfo roleGameInfo : game.getRoleIdMap().values()) {
            verifyManager.accumlate(roleGameInfo.verify);
        }
        game.setGameState(GameState.GAME_STATE_WAIT);
        // 回合数+1
        int finishRoundCount = game.getFinishRoundCount() + 1;
        game.setFinishRoundCount(finishRoundCount);

        ICompareGameRule<Game> rule = game.getRule();

        game.roundInfoMap.setCurrentRoundCount(finishRoundCount);
        game.roundInfoMap.initRoundInfo(game.getRoleIdMap().keySet());

        rule.getRoundResult(game).getRoundResult(game);

        // 回合信息
        Builder scFightRoundOver = SCFightRoundOver.newBuilder();

        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            RoundInfo roundInfo = game.roundInfoMap.getRoundInfo(info.gameRoleId);
            int chipMoney = roundInfo.score;

            Role role = RoleCache.getRoleById(info.roleId);
            role.setGold(role.getGold() + chipMoney);

            // 每个人回合信息
            RoleRoundOverInfoData.Builder roleRoundOverInfoDataRuilder = RoleRoundOverInfoData.newBuilder()
                    .setGameRoleData(matchService.parseGameRoleData(info, game))
                    .setRoleId(info.roleId)
                    .setBetMoney(info.betScoreRecord)
                    .setCardData(CardData.newBuilder().addAllCards(roundInfo.cards).setCardType(roundInfo.cardTpyeId))
                    .setOverMethod(roundInfo.overMethod)
//                    .setRoundScore(roundInfo.point)
//                    .setChipMoney(chipMoney)
                    .setGold(chipMoney)
                    .setZhuang(info.seat == game.getZhuangSeat());

            // 设置每个游戏的特殊信息
            rule.setRoundOverInfo(roleRoundOverInfoDataRuilder, roundInfo, game);

            scFightRoundOver.addRoleRoundOverInfoData(roleRoundOverInfoDataRuilder);

        }

        SC sc = SC.newBuilder()
                .setSCFightRoundOver(
                        scFightRoundOver.setRoomId(game.getGameConfig().getRoomId())
                                .setFinishRoundCount(finishRoundCount)
                                .setMaxRoundCount(game.getGameConfig().getRoundCount()))
                .build();

        // 发送
        gameBroadcast.broadcast(game, sc);
        eventBus.post(new EventNoticeRoundOver(game, sc));

        // 金币模式不用存储roundInfo
        game.roundInfoMap.remove(finishRoundCount);

    }

}
