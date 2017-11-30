package com.randioo.compare_collections_server.module.exit.service;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface ExitService extends ObserveBaseServiceInterface {

	/**
	 * 申请退出
	 * 
	 * @param role
	 * @author wcy 2017年9月22日
	 */
	void applyExitGame(Role role);

	/**
	 * 同意退出
	 * 
	 * @param role
	 * @param vote
	 * @param voteId
	 * @author wcy 2017年9月22日
	 */
	void agreeExit(Role role, int vote, int voteId);

	/**
	 * 还没开始的时候退出游戏
	 * 
	 * @param role
	 */
	void exitGame(Role role);

    /**
     * 解散游戏
     *
     * @param game
     */
    void dismissGame(Game game);

}
