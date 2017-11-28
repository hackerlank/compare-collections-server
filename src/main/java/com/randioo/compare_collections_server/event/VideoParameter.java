/**
 * 
 */
package com.randioo.compare_collections_server.event;

import java.util.List;

import com.randioo.randioo_server_base.eventbus.Event;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月22日 下午4:30:52
 */
public class VideoParameter implements Event {
    public String msg;
    public List<Object> args;
}
