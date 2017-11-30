package com.randioo.compare_collections_server.module.fight.component.manager;

import org.springframework.stereotype.Component;

import com.randioo.randioo_server_base.entity.Verify;
import com.randioo.randioo_server_base.service.AbstractVerifyManager;

@Component
public class VerifyManager extends AbstractVerifyManager<Verify> {

    @Override
    public int getVerifyId(Verify t) {
        return t.verifyId;
    }

    @Override
    protected int getAccumlate(Verify t) {
        return t.useId;
    }

    @Override
    protected void verifyId(Verify t, int newVerifyId) {
        t.verifyId = newVerifyId;
    }

    @Override
    protected void accumlateToValue(Verify t, int accumlate) {
        t.useId = accumlate;
    }

    @Override
    public void accumlate(Verify t) {
        t.useId++;
    }
}
