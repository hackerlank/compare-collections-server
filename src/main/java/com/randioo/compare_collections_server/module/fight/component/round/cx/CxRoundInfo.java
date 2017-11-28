package com.randioo.compare_collections_server.module.fight.component.round.cx;

import com.randioo.compare_collections_server.entity.file.CXCardTypeConfig;
import com.randioo.compare_collections_server.module.fight.component.round.RoundInfo;

public class CxRoundInfo extends RoundInfo {
    /** 牌型 */
    public CXCardTypeConfig cxCardTypeConfig;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("CxRoundInfo [cxCardTypeConfig=")
                .append(cxCardTypeConfig)
                .append(", overMethod=")
                .append(overMethod)
                .append(", cards=")
                .append(cards)
                .append(", score=")
                .append(score)
                .append(", cardTpyeId=")
                .append(cardTpyeId)
                .append(", point=")
                .append(point)
                .append("]");
        return builder.toString();
    }

}
