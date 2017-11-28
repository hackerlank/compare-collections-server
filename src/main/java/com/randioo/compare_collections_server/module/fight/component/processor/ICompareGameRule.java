package com.randioo.compare_collections_server.module.fight.component.processor;

import com.google.protobuf.Message;
import com.randioo.compare_collections_server.entity.bo.Role;
import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.rule.IReconnector;
import com.randioo.compare_collections_server.module.fight.component.rule.ISafeCheckCallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;
import com.randioo.compare_collections_server.protocol.Entity.GameRoleData;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData;

import java.util.List;

/**
 * 比较游戏的规则
 *
 * @param <T>
 * @author wcy 2017年10月29日
 */
public interface ICompareGameRule<T> extends ICommandCallback<T> {
    String WAIT = "wait";

    /**
     * 获得所有卡牌
     *
     * @return
     * @author wcy 2017年10月29日
     */
    List<Integer> getCards();

    /**
     * @param game
     * @return
     * @author wcy 2017年10月29日
     */
    void checkCallTypes(T game);

    /**
     * 初始化数据结构
     * 
     * @param game
     * @author wcy 2017年11月4日
     */
    void initDataStructure(T game);

    /**
     * 设置回合结束的协议
     * 
     * @param roundOverInfoDataBuilder
     * @param roundInfo
     * @param game
     */
    void setRoundOverInfo(RoleRoundOverInfoData.Builder builder, RoundInfo roundInfo, T game);

    RoundOverCaculator getRoundResult(T game);

    Class<? extends RoundInfo> getRoundInfoClass();

    /**
     * 通过枚举获得操作
     * 
     * @param callTypeEnum
     * @return
     * @author wcy 2017年11月6日
     */
    CallType getCallTypeByEnum(CallTypeEnum callTypeEnum);

    /**
     * 获得庄判断器
     * 
     * @return
     * @author wcy 2017年11月8日
     */
    ZhuangCreator getZhuangCreator();

    ISafeCheckCallType getSafeCheckCallType();

    /**
     * 获得重连器
     * 
     * @param game
     * @return
     * @author wcy 2017年11月20日
     */
    IReconnector<Game, Role, Message> getReconnector(T game);
}
