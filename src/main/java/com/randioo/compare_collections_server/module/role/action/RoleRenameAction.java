package com.randioo.compare_collections_server.module.role.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import com.google.protobuf.GeneratedMessage;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.module.role.service.RoleService;
import com.randioo.compare_collections_server.protocol.Role.RoleRenameRequest;
import com.randioo.randioo_server_base.annotation.PTAnnotation;
import com.randioo.randioo_server_base.cache.RoleCache;
import com.randioo.randioo_server_base.template.IActionSupport;
import com.randioo.randioo_server_base.utils.SessionUtils;

@Controller
@PTAnnotation(RoleRenameRequest.class)
public class RoleRenameAction implements IActionSupport {

    @Autowired
    private RoleService roleService;

    @Override
    public void execute(Object data, Object session) {
        RoleRenameRequest request = (RoleRenameRequest) data;
        Role role = RoleCache.getRoleBySession(session);
        GeneratedMessage sc = roleService.rename(role, request.getName());
        SessionUtils.sc(session, sc);
    }

}
