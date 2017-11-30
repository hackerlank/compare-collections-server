package com.randioo.compare_collections_server.module.fight.component.parser;

import com.randioo.compare_collections_server.cache.file.YiyaAutoBetConfigCache;
import com.randioo.compare_collections_server.entity.file.YiyaAutoBetConfig;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;
import com.randioo.compare_collections_server.protocol.Entity.GameType;
import com.randioo.randioo_server_base.config.GlobleClass;
import com.randioo.randioo_server_base.template.Parser;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-20 16:46
 **/
@Component
public class GameConfigProtoParser implements Parser<GameConfigData, Integer> {
	@Override
	public GameConfigData parse(Integer matchParameter) {
		Map<Integer, YiyaAutoBetConfig> cardMap = YiyaAutoBetConfigCache.getYiYaAutoBetMap();
		GameConfigData.Builder gameConfig = GameConfigData.newBuilder()
                .setRoundCount(GlobleClass._G.round_count)
				.setZhuangType(GlobleClass._G.zhuang_type)// 庄类型
				.setMaxCount(GlobleClass._G.max_count)// 最大 人数
                .setMinCount(GlobleClass._G.min_count)// 最小 人数
                .setGameType(GameType.GAME_TYPE_GOLD.getNumber())// 金币场
                // 扎金花
                .setOutLookCount(GlobleClass._G.zjh.out_look_count)// 看牌 轮数
                .setFightRound(GlobleClass._G.zjh.fight_round)// 比赛 轮数
                .setTopCount(GlobleClass._G.zjh.top_count)// 开牌轮数
                //
				.setBetMax(GlobleClass._G.cx.bet_max);//

        YiyaAutoBetConfig config = cardMap.get(matchParameter);
        gameConfig.setBet(config.autoBet);
		List<Integer> list = new ArrayList<>();
		list.add(config.bet1);
		list.add(config.bet2);
		list.add(config.bet3);
		list.add(config.bet4);
		list.add(config.bet5);

		gameConfig.addAllBattleList(list);

		return gameConfig.build();
	}
}
