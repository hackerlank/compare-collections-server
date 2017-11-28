package com.randioo.compare_collections_server.module.login.service;

import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.protocol.Entity.RoleData;
import com.randioo.randioo_server_base.module.login.LoginInfo;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface LoginService extends ObserveBaseServiceInterface {

    GeneratedMessage getRoleData(LoginInfo loginInfo, Object ioSession);

    // GeneratedMessage creatRole(String account);
    //
    // GeneratedMessage login(String account);

    /**
     * 通过id获取玩家
     * 
     * @param roleId
     * @return
     * @author wcy 2017年1月10日
     */
    public Role getRoleById(int roleId);

    /**
     * 通过帐号获得玩家
     * 
     * @param account
     * @return
     * @author wcy 2017年1月10日
     */
    public Role getRoleByAccount(String account);

    RoleData getRoleData(Role role);

}
