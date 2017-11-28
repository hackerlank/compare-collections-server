package com.randioo.compare_collections_server.module.fight.component;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.randioo.compare_collections_server.entity.po.Game;

/**
 * 游戏监听器
 * 
 * @author wcy 2017年8月25日
 * 
 */
public interface Flow {

    static final Logger logger = LoggerFactory.getLogger(Flow.class);

    public void execute(Game game, String[] params);

}
