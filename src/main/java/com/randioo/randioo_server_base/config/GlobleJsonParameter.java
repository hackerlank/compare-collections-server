package com.randioo.randioo_server_base.config;

import java.util.List;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-26 14:27
 **/
public class GlobleJsonParameter {

    /**
     * need_gold : 0
     * min_count : 2
     * max_count : 5
     * wait_time : 5
     * zhuang_type : 4
     * match_sort : [4,3,2,1]
     * start_time : 8
     * round_count : 4
     * sdb : {"default_bet_score":1,"defalut_choose_card":false,"bet_list":[1,5,10]}
     * zjh : {"out_look_count":0,"fight_round":1,"top_count":15}
     * cx : {"bet_max":20}
     */

    public int need_gold;
    public int min_count;
    public int max_count;
    public int wait_time;
    public int zhuang_type;
    public int start_time;
    public int round_count;
    public SdbBean sdb;
    public ZjhBean zjh;
    public CxBean cx;
    public List<Integer> match_sort;

    public static class SdbBean {
        /**
         * default_bet_score : 1
         * defalut_choose_card : false
         * bet_list : [1,5,10]
         */

        public int default_bet_score;
        public boolean defalut_choose_card;
        public List<Integer> bet_list;
    }

    public static class ZjhBean {
        /**
         * out_look_count : 0
         * fight_round : 1
         * top_count : 15
         */

        public int out_look_count;
        public int fight_round;
        public int top_count;
    }

    public static class CxBean {
        /**
         * bet_max : 20
         */

        public int bet_max;
    }
}
