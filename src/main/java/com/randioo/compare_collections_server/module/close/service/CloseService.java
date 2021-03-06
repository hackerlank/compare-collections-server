package com.randioo.compare_collections_server.module.close.service;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.randioo_server_base.service.BaseServiceInterface;

public interface CloseService extends BaseServiceInterface {
    void asynManipulate(Role role);

    void roleDataCache2DB(Role role, boolean mustSave);

    void beforeCloseHandle(Role role);
}
