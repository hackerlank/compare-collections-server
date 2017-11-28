package com.randioo.compare_collections_server.cache.local;

import com.randioo.compare_collections_server.module.match.component.MatchInfo;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-21 14:36
 **/
public class MatchCache {
    private static BlockingQueue<MatchInfo> waitQueue = new LinkedBlockingQueue<>();

    public static BlockingQueue<MatchInfo> getWaitQueue() {
        return waitQueue;
    }
}
