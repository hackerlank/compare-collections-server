package com.randioo.compare_collections_server.quartz;

import com.randioo.compare_collections_server.entity.po.Game;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-28 13:08
 **/
public class JobListener implements org.quartz.JobListener {
    @Autowired
    private QuartzManager quartzManager;

    @Override
    public String getName() {
        return "sdb";
    }

    @Override
    public void jobToBeExecuted(JobExecutionContext context) {
        JobDataMap map = context.getMergedJobDataMap();
        Game game = (Game) map.get("game");
        int seat = (int) map.get("seat");
        System.out.println("============执行前=================");
        System.out.println("game id  "+game.getGameId());
        if (game.getCurrentSeat() != seat) {
            System.out.println("座位号不相等，删除job");
            quartzManager.cancelJob(context.getJobDetail().getKey().getName());
        }
    }

    @Override
    public void jobExecutionVetoed(JobExecutionContext context) {
    }

    @Override
    public void jobWasExecuted(JobExecutionContext context, JobExecutionException jobException) {
        System.out.println("执行后删除");
        quartzManager.cancelJob(context.getJobDetail().getKey().getName());
    }
}
