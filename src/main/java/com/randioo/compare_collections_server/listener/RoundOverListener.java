package com.randioo.compare_collections_server.listener;

import org.springframework.stereotype.Component;

import com.google.common.eventbus.Subscribe;
import com.randioo.compare_collections_server.cache.local.RoundOverSCCache;
import com.randioo.compare_collections_server.module.fight.component.event.EventGameStart;
import com.randioo.compare_collections_server.module.fight.component.event.EventNoticeRoundOver;
import com.randioo.randioo_server_base.eventbus.Listener;

/**
 * 回合结算监听
 * 
 * @author wcy 2017年11月21日
 *
 */
@Component
public class RoundOverListener implements Listener {
    @Subscribe
    public void roundOver(EventNoticeRoundOver event) {
        RoundOverSCCache.put(event.getGame().getGameId(), event.sc);
    }

    @Subscribe
    public void ready(EventGameStart event) {
        RoundOverSCCache.clear(event.getGame().getGameId());
    }
}
