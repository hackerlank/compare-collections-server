package com.randioo.compare_collections_server.module.fight.component.rule;

import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;
import com.randioo.compare_collections_server.module.fight.component.round.RoundOverCaculator;
import com.randioo.compare_collections_server.module.fight.component.rule.base.CallType;
import com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum.CallTypeEnum;
import com.randioo.compare_collections_server.module.fight.component.zhuang.ZhuangCreator;
import com.randioo.compare_collections_server.protocol.Entity.RoleRoundOverInfoData.Builder;

import java.util.List;

public abstract class CompareGameRuleAdapter<T> implements ICompareGameRule<T> {

    @Override
    public abstract List<String> afterCommandExecute(T game, String flowName, String[] params);

    @Override
    public abstract List<Integer> getCards();

    @Override
    public abstract void checkCallTypes(T game);

    @Override
    public void initDataStructure(T game) {

    }

    @Override
    public abstract void setRoundOverInfo(Builder builder, RoundInfo roundInfo, T game);

    @Override
    public abstract RoundOverCaculator getRoundResult(T game);

    @Override
    public abstract Class<? extends RoundInfo> getRoundInfoClass();

    @Override
    public abstract CallType getCallTypeByEnum(CallTypeEnum callTypeEnum);

    @Override
    public ZhuangCreator getZhuangCreator() {
        return null;
    }

}
