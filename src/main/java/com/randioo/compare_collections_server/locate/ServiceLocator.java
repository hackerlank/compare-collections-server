package com.randioo.compare_collections_server.locate;

import java.lang.reflect.Proxy;

public class ServiceLocator {
    /**
     * 创建服务
     * 
     * @param address
     * @author wcy 2017年10月9日
     */
    public void create(int port) {

    }

    /**
     * 搜索服务
     * 
     * @param name
     * @author wcy 2017年10月9日
     */
    public Object lookup(String name, Class<?> clazz) {
        RemoteInvocationHandler handler = new RemoteInvocationHandler(name);
        Object proxy = Proxy.newProxyInstance(Thread.currentThread().getContextClassLoader(),
                new Class[] { ChatService.class }, handler);
        return proxy;
    }

    public static void main(String[] args) {
        ServiceLocator locator = new ServiceLocator();
        ChatService chatService = (ChatService) locator.lookup("192.18.0.1", ChatService.class);
        chatService.send("account");
    }

}
