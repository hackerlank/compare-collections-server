package com.randioo.compare_collections_server.comparator;

import java.util.Comparator;

/**
 * 座位比较器
 * 
 * @author wcy 2017年11月10日
 *
 */
public class SeatComparator implements Comparator<Integer> {
    private int total;
    private int offset;

    /**
     * 
     * @param total 总量
     * @param offset 偏移量
     */
    public SeatComparator(int total, int offset) {
        this.total = total;
        this.offset = offset;
    }

    @Override
    public int compare(Integer o1, Integer o2) {
        int n1 = o1 - offset;
        int n2 = o2 - offset;
        n1 = n1 < 0 ? n1 + total : n1;
        n2 = n2 < 0 ? n2 + total : n2;

        return n1 - n2;
    }
}
