/**
 * 
 */
package com.randioo.compare_collections_server.module.fight.component.zhuang;

import com.randioo.compare_collections_server.entity.po.Game;

/**
 * @Description: 产生庄家座位号
 * @author zsy
 * @date 2017年9月21日 下午5:48:24
 */
public interface ZhuangCreator {

    int getSeat(Game game);

}
