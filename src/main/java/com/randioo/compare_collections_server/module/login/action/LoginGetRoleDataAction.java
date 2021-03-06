package com.randioo.compare_collections_server.module.login.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.module.login.component.LoginConfig;
import com.randioo.compare_collections_server.module.login.service.LoginService;
import com.randioo.compare_collections_server.protocol.Login.LoginGetRoleDataRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(LoginGetRoleDataRequest.class)
public class LoginGetRoleDataAction implements IActionSupport {

    @Autowired
    private LoginService loginService;

    @Override
    public void execute(Object data, Object session) {
        LoginGetRoleDataRequest request = (LoginGetRoleDataRequest) data;

        // 建立登陆配置
        LoginConfig loginConfig = new LoginConfig();
        loginConfig.setAccount(request.getAccount());
        loginConfig.setHeadImageUrl(request.getHeadImageUrl());
        loginConfig.setMacAddress(request.getUuid());
        loginConfig.setNickname(request.getNickname());

        GeneratedMessage sc = loginService.getRoleData(loginConfig, session);
        SessionUtils.sc(session, sc);
    }

}
