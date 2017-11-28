package com.randioo.compare_collections_server.handler;

import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.keepalive.KeepAliveFilter;
import org.apache.mina.filter.keepalive.KeepAliveRequestTimeoutHandler;
import org.springframework.beans.factory.annotation.Autowired;

import com.randioo.compare_collections_server.module.close.service.CloseService;

public class HeartTimeOutHandler implements KeepAliveRequestTimeoutHandler {

    @Autowired
    private CloseService closeService;

    @Override
    public void keepAliveRequestTimedOut(KeepAliveFilter arg0, IoSession arg1) throws Exception {
        // Role role = (Role) RoleCache.getRoleBySession(arg1);
        // System.out.println("调用 " + role.getAccount());

        // System.out.println(TimeUtils.getDetailTimeStr() +
        // " keepAliveRequestTimedOut");
        // arg1.close(true);
        // Role role = (Role) RoleCache.getRoleBySession(arg1);
        // closeService.asynManipulate(role);
        //
        // arg1.close(true);
    }
}
