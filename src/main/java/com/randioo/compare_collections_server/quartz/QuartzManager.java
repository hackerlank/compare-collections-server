package com.randioo.compare_collections_server.quartz;

import com.randioo.randioo_server_base.utils.TimeUtils;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.quartz.DateBuilder.futureDate;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-28 9:54
 **/
@Component
public class QuartzManager {
    @Autowired
    private Scheduler quartzScheduler;

    public void addJob(Class classz, int startTime, String gameRoleId, Map<String, Object> parameter) {
        String key = gameRoleId + TimeUtils.getNowTime();

        JobDetail jobDetail = JobBuilder.newJob(classz).withIdentity(JobKey.jobKey(key,"sdb")).build();
        //设置参数
        JobDataMap map = jobDetail.getJobDataMap();
        map.putAll(parameter);


        Trigger trigger = TriggerBuilder.newTrigger().withIdentity(TriggerKey.triggerKey(key)).forJob(jobDetail)
                .startAt(futureDate(startTime, IntervalUnit.SECOND)).build();
        try {
            quartzScheduler.scheduleJob(jobDetail, trigger);
            System.out.println("添加一个定时器 key : " + key);
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
    }

    public void cancelJob(String gameRoleId) {
        TriggerKey triggerKey = TriggerKey.triggerKey(gameRoleId);
        try {
            quartzScheduler.pauseTrigger(triggerKey);// 停止触发器
            quartzScheduler.unscheduleJob(triggerKey);// 移除触发器
            quartzScheduler.deleteJob(JobKey.jobKey(gameRoleId));// 删除任务
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

    }
}
