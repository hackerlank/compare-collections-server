package com.randioo.compare_collections_server.module.fight.component.parser;

import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.GameManager;
import com.randioo.compare_collections_server.module.fight.component.manager.RoleGameInfoManager;
import com.randioo.compare_collections_server.module.fight.component.round.GameOverResult;
import com.randioo.compare_collections_server.protocol.Entity.ScoreData;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.Parser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-17 9:47
 **/
@Component
public class ScoreProtoParser implements Parser<ScoreData, RoleGameInfo> {
    @Autowired
    private RoleGameInfoManager roleGameInfoManager;

    @Autowired
    private GameManager gameManager;

    @Override
    public ScoreData parse(RoleGameInfo roleGameInfo) {
        Role role = (Role) RoleCache.getRoleById(roleGameInfo.roleId);
        Game game = GameCache.getGameMap().get(role.getGameId());

        int score =0;
        if(gameManager.isGoldMode(game)){
            score = role.getGold();
        }else{
            Map<String, GameOverResult> resultMap = game.getStatisticResultMap();
            if (resultMap.size() != 0) {
                GameOverResult gameOverResult = resultMap.get(roleGameInfo.gameRoleId);
                if (gameOverResult != null) {
                    score = resultMap.get(roleGameInfo.gameRoleId).score;
                }
            }
        }

        return ScoreData.newBuilder()
                .setScore(score)
                .setChipMoney(roleGameInfo.chipMoney)
                .setSeat(roleGameInfo.seat)
                .build();
    }
}
