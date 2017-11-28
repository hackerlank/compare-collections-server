package com.randioo.compare_collections_server.scheduler.timeevent;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.processor.Processor;
import com.randioo.compare_collections_server.protocol.Entity.GameState;
import com.randioo.randioo_server_base.scheduler.DefaultTimeEvent;
import com.randioo.randioo_server_base.scheduler.TimeEvent;
import com.randioo.randioo_server_base.utils.SpringContext;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-22 14:42
 **/
public class GameStartEvent extends DefaultTimeEvent {
    private Game game;

    public GameStartEvent(Game game) {
        this.game = game;
    }

    @Override
    public void update(TimeEvent timeEvent) {
        synchronized (game) {
            if (game.getRoleIdMap().size() <= 1) {
                // 人数不够能开始
                return;
            }

            // 如果游戏已经开始则不再开始
            if (game.getGameState() == GameState.GAME_STATE_START) {
                return;
            }
            Processor processor = SpringContext.getBean(Processor.class);
            processor.nextProcess(game, "role_game_start");
        }
    }
}
