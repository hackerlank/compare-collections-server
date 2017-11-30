package com.randioo.compare_collections_server.module.fight.component.parser;

import org.springframework.stereotype.Component;

import com.randioo.compare_collections_server.protocol.Fight.SCFightCountdown;
import com.randioo.compare_collections_server.protocol.ServerMessage.SC;
import com.randioo.randioo_server_base.template.Parser;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-29 13:36
 **/
@Component
public class CountdownProtoParser implements Parser<SC, Integer> {
    @Override
    public SC parse(Integer param) {
        return SC.newBuilder().setSCFightCountdown(SCFightCountdown.newBuilder().setCountdown(param)).build();
    }
}
