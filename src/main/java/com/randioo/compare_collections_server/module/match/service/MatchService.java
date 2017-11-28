package com.randioo.compare_collections_server.module.match.service;

import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.module.key.Key;
import com.randioo.randioo_server_base.service.ObserveBaseServiceInterface;

public interface MatchService extends ObserveBaseServiceInterface {
    /**
     * 创建游戏
     *
     * @param role
     * @return
     * @author wcy 2017年5月25日
     */
    public void createRoom(Role role, GameConfigData gameConfigData);

    /**
     * 加入游戏
     *
     * @param role
     * @param lockString
     * @return
     */
    void joinInRoom(Role role, String lockString);

    void match(Role role,int matchParameter);

    Game createGame(int roleId, GameConfigData gameConfigData);

    Game createGameByGameConfig(GameConfigData gameConfigData, GameType gameType);

    GameRoleData parseGameRoleData(RoleGameInfo info, Game game);

    GameRoleData parseAudienceGameRoleData(Role role, Game game, int seat);

    void fillAI(Game game);

    /**
     * 获得钥匙的房间字符串
     *
     * @param key
     * @return
     * @author wcy 2017年7月13日
     */
    String getLockString(Key key);

    /**
     * 取消匹配
     *
     * @param role
     * @author wcy 2017年7月14日
     */
    void cancelMatch(Role role);

    /**
     * 取消匹配服务接口
     *
     * @param role
     * @author wcy 2017年7月14日
     */
    void serviceCancelMatch(Role role);

    void checkRoom(String roomId, Object session);

    /**
     * 清空某玩家的座位
     *
     * @param game
     * @param seat
     * @author wcy 2017年7月28日
     */
    void clearSeatByGameRoleId(Game game, String gameRoleId);

    void matchJoinGame(Role role, String roomId);

    /**
     * 添加用户
     *
     * @param game
     * @param roleId
     * @author wcy 2017年11月22日
     */
    void addAccountRole(Game game, int roleId);

    /**
     * 观众中途加入
     *
     * @param role
     * @param lockString
     */
    void audienceJoinGame(Role role, String lockString);

}
