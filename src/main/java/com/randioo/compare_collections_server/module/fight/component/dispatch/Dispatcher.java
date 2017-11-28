/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.dispatch;

import java.util.List;

/**
 * @Description:
 * @author zsy
 * @date 2017年9月22日 下午3:32:07
 */
public interface Dispatcher {
    public List<CardPart> dispatch(List<Integer> remainCards, int partCount, int everyPartCount);
}
