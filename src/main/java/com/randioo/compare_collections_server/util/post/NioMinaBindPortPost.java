package com.randioo.compare_collections_server.util.post;

import java.io.IOException;
import java.net.InetSocketAddress;

import org.apache.mina.core.service.IoAcceptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.Ordered;

import com.randioo.randioo_server_base.GlobleConstant;
import com.randioo.randioo_server_base.config.GlobleMap;

public class NioMinaBindPortPost implements BeanPostProcessor, Ordered {
    protected Logger logger = LoggerFactory.getLogger(NioMinaBindPortPost.class);

    @Override
    public Object postProcessAfterInitialization(Object arg0, String arg1) throws BeansException {
        if (arg0 instanceof IoAcceptor) {
            final IoAcceptor ioAcceptor = (IoAcceptor) arg0;
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        Thread.sleep(2000);
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }
                    InetSocketAddress inetSocketAddress = new InetSocketAddress(GlobleMap.Int(GlobleConstant.ARGS_PORT));
                    try {
                        ioAcceptor.bind(inetSocketAddress);
                        logger.info("WANSERVER : START SERVER SUCCESS -> socket port:{}", inetSocketAddress.getPort());
                        GlobleMap.putParam(GlobleConstant.ARGS_LOGIN, true);
                    } catch (IOException e) {
                        logger.error("", e);
                    }

                }
            }).start();

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
        return 4;
    }

}
