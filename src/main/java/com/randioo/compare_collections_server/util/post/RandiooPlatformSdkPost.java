package com.randioo.compare_collections_server.util.post;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import com.randioo.compare_collections_server.GlobleConstant;
import com.randioo.randioo_server_base.config.GlobleMap;
import com.yiya.yiya_platform_sdk.YiyaPlatformSdk;

public class RandiooPlatformSdkPost implements BeanPostProcessor, Ordered {
    private Logger logger = LoggerFactory.getLogger(RandiooPlatformSdkPost.class);

    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
        if (arg0 instanceof YiyaPlatformSdk) {
            YiyaPlatformSdk sdk = (YiyaPlatformSdk) arg0;
            String activeProject = GlobleMap.String(GlobleConstant.ARGS_PLATFORM_PACKAGE_NAME);
            sdk.setActiveProjectName(activeProject);
            logger.info("randiooPlatformSdkPost init {}", activeProject);
        }
        return arg0;
    }

    @Override
    public Object postProcessBeforeInitialization(Object arg0, String arg1) throws BeansException {
        return arg0;
    }

    @Override
    public int getOrder() {
        // TODO Auto-generated method stub
        return 0;
    }

}
