package com.randioo.compare_collections_server.module.fight;

import java.util.Arrays;
import java.util.List;

public class FightConstant {

	public final static String SEND_CARD = "fight_send_card";

	public static final String NEXT_GAME_ROLE_SEND_CARD = "next_game_role_send_card";
	public static final String ROUND_OVER = "fight_round_over";
	public static final String FIGHT_GAME_OVER = "fight_game_over";
	public static final String FIGHT_NOTICE_SEND_CARD = "fight_notice_send_card";
	public static final String FIGHT_GANG_PENG_HU = "fight_gang_peng_hu";

	public static final String FIGHT_GANG_PENG_HU_OVER = "fight_gang_peng_hu_over";
	public static final String FIGHT_APPLY_LEAVE = "fight_apply_leave";
	public static final String FIGHT_READY = "fight_ready"; // 准备
	public static final String FIGHT_GAME_EXIT = "fight_exit_game";

	public static final String FIGHT_CANCEL_GAME = "fight_cancel_game";

	public static final int SCORE_3 = 3;

	public static final int SEND_CARD_WAIT_TIME = 30;

	public static final int COUNTDOWN = 30;

	public static final int GAME_IDLE = 1;
	public static final int GAME_OVER = 2;
	public static final int GAME_CONTINUE = 3;

	public static final String FIGHT_RECORD_SC = "record_sc"; // 记录推送

	public static final String FIGHT_START = "start"; // 开始游戏
	public static final String FIGHT_TOUCH_CARD = "touch_card";// 摸牌
	public static final String FIGHT_SEND_CARD = "send_card"; // 出牌
	public static final String FIGHT_COUNT_DOWN = "count_down"; // 倒计时
	public static final String FIGHT_POINT_SEAT = "point_seat";// 座位指针
	public static final String FIGHT_VOTE_APPLY_EXIT = "vote_exit";// 投票退出
	public static final String FIGHT_DISMISS = "fight_dismiss";// 房间解散

	public static final String FIGHT_GUO = "guo"; // 过

	public static final String FIGHT_SCORE = "fight_score";// 分数改变

	/**
	 * 每个玩家初始的牌数量
	 */
	public static final int EVERY_INIT_CARD_COUNT = 1;
	/**
	 * 十点半初始的牌数量
	 */
	public static final int TEN_THIRTY_EVERY_INIT_CARD_COUNT = 1;
	/**
	 * 扎金花初始的牌数量
	 */
	public static final int ZJH_EVERY_INIT_CARD_COUNT = 3;
	public static final String FIGHT_WATCH = "watch";
	public static final String FIGHT_CLOSE_BUTTON = "close button";
	public static final int FIGHT_MAX_COUNT = 6;
	public static final int FIGHT_MIN_COUNT = 2;
	public static final int FIGHT_START_MONEY = 1;
	public static final String FIGHT_ROLES_NOT_ENOUGHT = "out of the min count";
	public static final String FIGHT_ROLES_NOT_READY = "there is someone without readying";
	public static final String FIGHT_OPEN_ALL_CARDS = "fight_open_all_cards"; // 全部开牌
	public static final String FIGHT_BET_DISAPPEAR = "close bet button"; //

    public static final int ZHUANG_FREEDOM = 1;// (自由抢庄)
    public static final int ZHUANG_ROOM_OWNER = 2;// (房主庄家)
    public static final int ZHUANG_ORDER = 3;// 轮流坐庄
    public static final int ZHUANG_GOLD = 4;//金币模式庄家

	public static final int LOSS = 0;
	public static final int WIN = 1;

	public static final int PAY_MODE_MASTER = 1;// 房主付款
	public static final int PAY_MODE_AA = 2;// AA付款

	public static final int CARD_TYPE_BAO_PAI = 25;// 爆牌
	public static final int CARD_TYPE_5 = 26;// 特殊牌型

    public static final List<Integer> SHOW_CARD_TYPE = Arrays.asList(1, 2, 3, 4, 25);

}
