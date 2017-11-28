package com.randioo.compare_collections_server.util.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import com.randioo.randioo_server_base.service.ServiceManager;

public class ServiceManagerPost implements BeanPostProcessor, Ordered {
    protected Logger logger = LoggerFactory.getLogger(ServiceManagerPost.class);

    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
        if (arg0 instanceof ServiceManager) {
            ServiceManager serviceManager = (ServiceManager) arg0;
            logger.info("initServices {}", getOrder());
            serviceManager.initServices();
        }
        return arg0;

    }

    @Override
    public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
        return arg0;
    }

    @Override
    public int getOrder() {
        return 2;
    }

}
