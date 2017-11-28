package com.randioo.compare_collections_server.util.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

public abstract class AbstractOrderedBeanPostProcessor implements BeanPostProcessor, Ordered {
    protected Logger logger = LoggerFactory.getLogger(this.getClass());

    private int order;

    public void setOrder(int order) {
        this.order = order;
    }

    @Override
    public int getOrder() {
        return order;
    }
}
