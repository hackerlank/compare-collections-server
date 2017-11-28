package com.randioo.compare_collections_server.locate;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.net.InetSocketAddress;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoConnector;
import org.apache.mina.core.session.IoSession;

import com.randioo.randioo_server_base.utils.ObjectUtils;
import com.randioo.randioo_server_base.utils.SpringContext;

public class RemoteInvocationHandler implements InvocationHandler {
    private String name;
    private int port;
    private IoSession session;

    public RemoteInvocationHandler(String name) {
        this.name = name;
    }

    private synchronized void init() {
        IoConnector connector = SpringContext.getBean(IoConnector.class);
        ConnectFuture future = connector.connect(new InetSocketAddress(port));
        future.awaitUninterruptibly();

        if (!future.isConnected()) {
            return;
        }

        IoSession session = future.getSession();
        this.session = session;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {

        if (session == null || !session.isConnected()) {
            init();
        }

        String name = method.getName();
        MethodInvoker methodInvoker = new MethodInvoker();
        methodInvoker.methodName = name;
        methodInvoker.args = args;

        byte[] bytes = ObjectUtils.serializeProtostuff(methodInvoker);
        session.write(bytes);

        return null;
    }
}
