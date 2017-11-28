package com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator;

import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.entity.po.RoleGameInfo;

/**
 * 扯玄顺序比较器
 * 
 * @author wcy 2017年11月16日
 *
 */
@Component
public class CxPreSequenceComparator implements Comparator<RoleGameInfo> {

    @Override
    public int compare(RoleGameInfo o1, RoleGameInfo o2) {
        List<Integer> head1 = CxUtils.get2Cards(o1, "head");
        List<Integer> head2 = CxUtils.get2Cards(o2, "head");
        List<Integer> tail1 = CxUtils.get2Cards(o1, "tail");
        List<Integer> tail2 = CxUtils.get2Cards(o2, "tail");

        int value = CxUtils.forceCompareCards(head1, head2);
        if (value == 0) {
            value = CxUtils.forceCompareCards(tail1, tail2);
        }

        return value;
    }

}
