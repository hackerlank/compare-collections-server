package com.randioo.compare_collections_server.module.gm.service;

import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface GmService extends ObserveBaseServiceInterface {

    void loopSaveData(boolean mustSave);

    // public GeneratedMessage rejectLogin(String code);
    // public void terminatedServer(String code,IoSession session);
    // public GeneratedMessage openLogin(String code);
    /**
     * 解散游戏
     * 
     * @param args
     * @author wcy 2017年11月21日
     */
    void dimissGame(String[] args);
}
