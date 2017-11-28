package com.randioo.compare_collections_server.scheduler;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.GlobleConstant;
import com.randioo.compare_collections_server.cache.local.GameCache;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.cache.SessionCache;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.randioo.randioo_server_base.utils.JedisUtils;

/**
 * 服务器监控定时器
 * 
 * @author wcy 2017年11月21日
 *
 */
@Component
public class ServerMonitorScheduler {

    @Scheduled(cron = "0/5 * * * * ?")
    public void collect() {
        try {
            String project = GlobleMap.String(GlobleConstant.ARGS_PROJECT_NAME);
            JedisUtils.set(project + "_session_count", String.valueOf(SessionCache.getAllSession().size()));
            JedisUtils.set(project + "_role_count", String.valueOf(RoleCache.getRoleAccountMap().size()));
            JedisUtils.set(project + "_game_count", String.valueOf(GameCache.getGameMap().size()));
        } catch (Exception e) {

        }
    }
}
