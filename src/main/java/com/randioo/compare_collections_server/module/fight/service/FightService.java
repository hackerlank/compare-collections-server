package com.randioo.compare_collections_server.module.fight.service;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

import java.util.List;

public interface FightService extends ObserveBaseServiceInterface {

    /**
     * 准备
     * 
     * @param role
     */
    void ready(Role role);

    /**
     * 检查所有人准备完毕
     * 
     * @param game
     * @return
     */
    boolean checkAllReady(Game game);

    /**
     * 叫分
     * 
     * @param score
     * @param role
     */
    void bet(int score, Role role);

    void bet(RoleGameInfo roleGameInfo, Game game, int score);

    /**
     * 继续要牌
     * 
     * @param role
     * @param needCard
     * @author wcy 2017年9月24日
     */
    void continueAddCard(Role role, boolean addCard);

    void coreContinueAddCard(Game game, RoleGameInfo roleGameInfo, boolean addCard);

    /**
     * 看牌
     * 
     * @param role
     * @param isWatchCard
     * @return
     */
    void watchCards(Role role);

    /**
     * 跟
     * 
     * @param role
     */
    void follow(Role role);

    /**
     * 大
     * 
     * @param role
     * @param betMoney
     * @author wcy 2017年10月16日
     */
    void bigger(Role role, int betMoney);

    /**
     * 敲
     * 
     * @param role
     * @author wcy 2017年10月16日
     */
    void betAll(Role role);

    /**
     * 休
     * 
     * @param role
     * @author wcy 2017年10月16日
     */
    void guo(Role role);

    /**
     * 放弃
     * 
     * @param role
     * @author wcy 2017年10月16日
     */
    void giveUp(Role role);

    void coreGiveUp(Game game, String gameRoleId);

    // /**
    // * 退出游戏
    // *
    // * @param role
    // */
    // void exitGame(Role role);
    //
    // /**
    // * 申请退出比赛
    // *
    // * @param role
    // * @return
    // */
    // void applyExitGame(Role role);

    /**
     * 开始游戏
     * 
     * @param role
     */
    void gameStart(Role role);

    //
    // void agreeExit(Role role, FightVoteApplyExit vote, int voteId);

    /**
     * 分牌
     * 
     * @param role
     */
    void cutCards(Role role, List<Integer> cards);

    /**
     * 对决
     * 
     * @param role
     * @param seat
     */
    void fight(Role role, int seat);

    /**
     * 重连
     * 
     * @param role
     */
    void reconnect(Role role);

    /**
     * 切牌核心
     * 
     * @param game
     * @param gameRoleId
     * @param cards
     * @author wcy 2017年11月30日
     */
    void coreCutCards(Game game, String gameRoleId, List<Integer> cards);

    /**
     * 
     * @param game
     * @param gameRoleId
     * @author wcy 2017年11月30日
     */
    void coreBetAll(Game game, String gameRoleId);
}
