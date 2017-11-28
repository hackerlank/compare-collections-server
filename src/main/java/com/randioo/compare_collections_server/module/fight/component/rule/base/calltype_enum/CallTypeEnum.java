package com.randioo.compare_collections_server.module.fight.component.rule.base.calltype_enum;

public enum CallTypeEnum {
    FOLLOW(0), // 跟注
    BIGGER(1), // 押注（比上一家更大）
    GIVE_UP(2), // 弃牌
    BET_ALL(3), // 敲
    GUO(4), // 过
    WATCH(5), // 看
    BET(2), // 十点半押注
    CHOOSE_ADD_CARD(1), // 要不要牌
    BATTLE(8), // 对决
    BIGGER_1(11), // 大1
    BIGGER_2(12), // 大2
    BIGGER_3(13)// 大3
    ;

    private CallTypeEnum(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }

    private int value;

}
