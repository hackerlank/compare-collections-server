package com.randioo.compare_collections_server.util.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import com.randioo.randioo_server_base.scheduler.SchedulerManager;

public class SchedulerManagerPost implements BeanPostProcessor, Ordered {
    private static Logger logger = LoggerFactory.getLogger(SchedulerManagerPost.class);

    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
        if (arg0 instanceof SchedulerManager) {
            SchedulerManager schedulerManager = (SchedulerManager) arg0;
            logger.info("scheduler start{}", getOrder());
            schedulerManager.start();
        }
        return arg0;

    }

    @Override
    public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
        return arg0;
    }

    @Override
    public int getOrder() {
        return 3;
    }

}
