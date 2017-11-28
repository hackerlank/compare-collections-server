package com.randioo.compare_collections_server.module.fight.component.rule;

import java.util.List;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.protocol.Entity.GameConfigData;

/**
 * 麻将规则
 * 
 * @author wcy 2017年8月21日
 * 
 */
public abstract class Rule {
    public static final String WAIT = "wait";

    /**
     * 状态
     * 
     * @author wcy 2017年8月21日
     * 
     */
    public enum StateEnum {
        /** 等待操作 */
        STATE_WAIT_OPERATION,
        /** 通知游戏准备 */
        STATE_INIT_READY,
        /** 游戏准备 */
        STATE_ROLE_GAME_READY,
        /** 游戏开始 */
        STATE_GAME_START,
        /** 检查庄家 */
        STATE_CHECK_ZHUANG,

        /** 通知叫分 */
        STATE_SC_BET,
        /** 押注 */
        STATE_BET,
        /** 自动押注 */
        STATE_AUTO_BET,
        /** 主推喊话 */
        STATE_SC_CALLTYPE,
        /** 检查跟大敲休丢 */
        STATE_CHECK_CALLTYPE,
        /** 选择了喊话 */
        STATE_CHOSEN_CALLTYPE,
        /** 发牌 */
        STATE_DISPATCH,
        /** 通知发牌 */
        STATE_SC_DISPATCH,
        /** 第几圈发牌计数器+1 */
        STATE_ADD_LOOP_COUNT,
        /** 通知选择要不要牌 */
        STATE_SC_CHOOSE_ADD_CARD,
        /** 选择要不要牌 */
        STATE_CHOOSE_ADD_CARD,
        /** 通知游戏开始 */
        STATE_SC_GAME_START,
        /** 通知出牌 */
        STATE_SC_SEND_CARD,
        /** 出牌 */
        STATE_GAME_SEND_CARD,
        /** 摸牌 */
        STATE_ADD_CARD,
        /** 通知摸到的牌 */
        STATE_SC_ADD_CARD,
        /** 回合结束 */
        STATE_ROUND_OVER,
        /** 十点半回合结束 */
        STATE_TEN_HALF_ROUND_OVER,
        /** 游戏结束 */
        STATE_GAME_OVER,

        /** 玩家出牌 */
        STATE_ROLE_SEND_CARD,

        /** 下一个 */
        STATE_NEXT_SEAT,
        /** 上一个 */
        STATE_PREV_SEAT,
        /** 定位座位 */
        STATE_LOCATE_SEAT,

        /** 过 */
        STATE_GUO,
        /** 跟 */
        STATE_FOLLOW
    }

    /**
     * 游戏是否结束
     * @param game
     * @return
     */
    protected boolean isGameOver(Game game) {
        GameConfigData gameConfigData = game.getGameConfig();

        int roundCount = gameConfigData.getRoundCount();
        int finshRoundCount = game.getFinishRoundCount();

        if (finshRoundCount >= roundCount) {
            return true;
        }
        return false;
    }

    /**
     * 获得所有牌
     * 
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<Integer> getCards();

    /**
     * 执行前的处理
     * 
     * @param ruleGame
     * @param majiangStateEnum
     * @param currentSeat
     * @return
     */
    public List<String> beforeStateExecute(Game gme, String flowName, int currentSeat) {
        return null;
    }

    /**
     * 
     * @param majiangState
     * @return
     * @author wcy 2017年8月21日
     */
    public abstract List<String> afterStateExecute(Game game, String flowName, int currentSeat);

    /**
     * 回合结束
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    public abstract void executeRoundOverProcess(Game game, boolean checkHu);

    /**
     * 游戏结束
     * 
     * @param game
     * @author wcy 2017年8月28日
     */
    public abstract void executeGameOverProcess(Game game);

    /**
     * 喊话内容
     * 
     * @param game
     * @author wcy 2017年10月12日
     */
    public abstract void checkCurrentCallTypes(Game game);
}
