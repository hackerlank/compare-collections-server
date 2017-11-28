package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;

@Component
public class FlowCheckCallTypes implements Flow {
    @Override
    public void execute(Game game, String[] params) {
        // 所有喊话清空
        ICompareGameRule<Game> rule = game.getRule();
        rule.checkCallTypes(game);
    }
}
