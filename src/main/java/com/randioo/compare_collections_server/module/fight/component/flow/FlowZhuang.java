package com.randioo.compare_collections_server.module.fight.component.flow;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;

/**
 * 庄家位置
 *
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class FlowZhuang implements Flow {

    @Override
    public void execute(Game game, String[] params) {
        ICompareGameRule<Game> rule = game.getRule();
        ZhuangCreator zhuangCreator = rule.getZhuangCreator();
        int seat = zhuangCreator.getSeat(game);
        game.setZhuangSeat(seat);
    }
}
