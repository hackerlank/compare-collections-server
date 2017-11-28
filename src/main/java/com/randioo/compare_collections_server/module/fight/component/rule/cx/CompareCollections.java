package com.randioo.compare_collections_server.module.fight.component.rule.cx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import com.google.common.collect.Lists;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.rule.cx.comparator.CxComparator;
import com.randioo.randioo_server_base.config.ConfigLoader;

public class CompareCollections {
    /**
     * 
     * @param list
     * @param comparator
     * @return
     * @author wcy 2017年11月15日
     */
    public static <T> List<List<T>> sort(List<T> list, Comparator<T> comparator) {
        List<List<T>> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            // 取出第一个对象
            T o1 = list.get(i);
            // 到结果列表中进行比对
            NEXT_ELEMENT: {
                int rowIndex, columnIndex;

                for (rowIndex = 0; rowIndex < result.size(); rowIndex++) {
                    for (columnIndex = 0; columnIndex < result.get(rowIndex).size(); columnIndex++) {
                        List<T> columnList = result.get(rowIndex);
                        T o2 = columnList.get(columnIndex);
                        int value = comparator.compare(o1, o2);
                        if (value > 0) {
                            continue;
                        }
                        if (value < 0) {
                            columnList = new ArrayList<>();
                            result.add(rowIndex, columnList);
                        }
                        columnList.add(o1);
                        break NEXT_ELEMENT;
                    }
                }
                if (result.size() == rowIndex) {
                    result.add(new ArrayList<T>());
                }
                result.get(rowIndex).add(o1);
            }

        }
        return result;
    }
    
    

    public static void main(String[] args) {
        ConfigLoader.loadConfig("com.randioo.compare_collections_server.entity.file", "./config.zip");
        RoleGameInfo r1 = new RoleGameInfo();
        RoleGameInfo r2 = new RoleGameInfo();

        r1.cards.addAll(Arrays.asList(21, 5, 18, 30));
        r2.cards.addAll(Arrays.asList(4, 17, 20, 26));
        List<RoleGameInfo> list = Lists.newArrayList(r1, r2);
        System.out.println(CompareCollections.sort(list, new CxComparator()));

    }
}
