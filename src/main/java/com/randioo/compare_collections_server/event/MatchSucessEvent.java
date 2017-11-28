package com.randioo.compare_collections_server.event;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.match.component.MatchInfo;
import com.randioo.randioo_server_base.eventbus.Event;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-21 15:11
 **/
public class MatchSucessEvent implements Event {
    public List<MatchInfo> MatchInfoList = new ArrayList<>();
    public Game game;
}
