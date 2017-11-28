package com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator;

import java.util.Comparator;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

@Component
public class CxComparator implements Comparator<RoleGameInfo> {

    @Override
    public int compare(RoleGameInfo o1, RoleGameInfo o2) {
        int headRawCompareResult = CxUtils.compareCards(CxUtils.get2Cards(o1, "head"), CxUtils.get2Cards(o2, "head"));
        int tailRawCompareResult = CxUtils.compareCards(CxUtils.get2Cards(o1, "tail"), CxUtils.get2Cards(o2, "tail"));

        int headFilterValue = filter(headRawCompareResult);
        int tailFilterValue = filter(tailRawCompareResult);

        return filter(headFilterValue + tailFilterValue);
    }

    private int filter(int value) {
        if (value < 0) {
            return -1;
        } else if (value == 0) {
            return 0;
        } else {
            return 1;
        }
    }

}
