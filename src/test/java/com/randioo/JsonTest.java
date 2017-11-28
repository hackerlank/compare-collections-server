package com.randioo;

import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.config.GlobleJsonLoader;
import org.junit.Test;

import java.util.List;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-26 14:24
 **/
public class JsonTest extends SpringTestBase{
    @Test
    public void  sfsa(){
        GlobleJsonLoader.init("./config.json");

        List<Integer> list = GlobleClass._G.match_sort;

        System.out.println(list);
    }
}
