package com.randioo.compare_collections_server.quartz;

import com.randioo.compare_collections_server.entity.po.Game;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-27 17:58
 **/
public class Test {

    public static void main(String[] args) throws SchedulerException, InterruptedException {
        Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();

        scheduler.start();

        JobDetail job = newJob(HelloJob.class).withIdentity("job1", "group1").build();
        Game game = new Game();
        game.setGameId(11);
        job.getJobDataMap().put("game",game);

        Trigger trigger = newTrigger().withIdentity("trigger1", "group1").startNow()
                .withSchedule(simpleSchedule().withIntervalInSeconds(1).repeatForever()).build();

        scheduler.scheduleJob(job, trigger);

        Thread.sleep(60000);
        scheduler.shutdown();


    }
}
