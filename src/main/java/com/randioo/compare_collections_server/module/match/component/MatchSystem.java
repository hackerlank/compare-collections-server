package com.randioo.compare_collections_server.module.match.component;

import com.randioo.compare_collections_server.entity.bo.Role;

public interface MatchSystem {
    void cancel(Role role);

    void match(Role role, int matchParameter);
}
