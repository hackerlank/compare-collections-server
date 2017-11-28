package com.randioo.compare_collections_server.module.fight.component.zhuang;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.entity.po.RoleGameInfo;
import com.randioo.compare_collections_server.module.fight.component.manager.SeatManager;
import com.randioo.compare_collections_server.module.match.service.MatchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 扎金花庄家位置规则(随机)
 *
 * @author ji.zhang(1024696326@qq.com)
 * @version sin2.0
 */
@Component
public class ZJHZhuangCreator implements ZhuangCreator {
    @Autowired
    MatchService matchService;// 创建游戏房间

    @Autowired
    SeatManager seatManager;// 座位管理器

    @Override
    public int getSeat(Game game) {
        int seat = -1;// 还没有确定位置
        List<Integer> list = new ArrayList<>();
        // 获取所有场上的位置
        for (RoleGameInfo info : game.getRoleIdMap().values()) {
            list.add(info.seat);
        }
        Random random = new Random();
        seat = random.nextInt(list.size() - 1);
        return seat;
    }
}
