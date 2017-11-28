package com.randioo.compare_collections_server.listener;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.randioo.compare_collections_server.cache.file.GameRoundConfigCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.file.GameRoundConfig;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeRoundOver;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.module.role.service.RoleService;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData;
import com.randioo.compare_collections_server.protocol.Fight.SCFightRoundOver;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.eventbus.Listener;

/**
 * 消费监听
 * 
 * @author wcy 2017年11月22日
 *
 */
@Component
public class ConsumeListener implements Listener {

    @Autowired
    private LoginService loginService;
    @Autowired
    private RoleService roleService;

    @Subscribe
    public void listen(EventNoticeRoundOver roundOver) {
        Game game = roundOver.getGame();
        GameType gameType = game.getGameType();
        if (gameType == GameType.GAME_TYPE_MATCH) {// 只有匹配场要扣一丫币
            roomConsume(game);
        } else if (gameType == GameType.GAME_TYPE_GOLD) {
            goldConsume(game, roundOver.sc.getSCFightRoundOver());
        }

    }

    /**
     * 金币消费
     * 
     * @param game
     * @param scFightRoundOver
     * @author wcy 2017年11月22日
     */
    private void goldConsume(Game game, SCFightRoundOver scFightRoundOver) {
        List<RoleRoundOverInfoData> list = scFightRoundOver.getRoleRoundOverInfoDataList();
        for (RoleRoundOverInfoData data : list) {
            int roundScore = data.getRoundScore();
            Role role = RoleCache.getRoleById(data.getRoleId());
            int gold = roundScore > 0 ? roundScore : -Math.min(Math.abs(roundScore), role.getGold());
            roleService.addGold(role, gold);
        }
    }

    /**
     * 房卡消费
     * 
     * @param game
     * @author wcy 2017年11月22日
     */
    private void roomConsume(Game game) {
        if (game.getFinishRoundCount() != 1) {// 完成第一局游戏后扣除钱
            return;
        }

        GameConfigData config = game.getGameConfig();

        int roundCount = config.getRoundCount();
        GameRoundConfig gameRoundConfig = GameRoundConfigCache.getGameRoundByRoundCount(roundCount);
        int masterRoleId = game.getMasterRoleId();

        Role master = loginService.getRoleById(masterRoleId);
        roleService.addRandiooMoney(master, -gameRoundConfig.needMoney);
    }

}
