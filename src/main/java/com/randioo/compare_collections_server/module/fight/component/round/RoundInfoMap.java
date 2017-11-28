package com.randioo.compare_collections_server.module.fight.component.round;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import com.randioo.randioo_server_base.utils.ReflectUtils;

/**
 * 回合结算表
 * 
 * @author wcy 2017年11月7日
 *
 */
public class RoundInfoMap {
    private int currentRoundCount;
    private Class<? extends RoundInfo> roundInfoClazz;
    private Map<Integer, Map<String, RoundInfo>> roundMap = new HashMap<>();

    public Map<String, RoundInfo> getCurrentRoundMap() {
        initRoundMap(currentRoundCount);
        return roundMap.get(currentRoundCount);
    }

    public Map<String, RoundInfo> getRoundMapByRoundCount(int roundCount) {
        initRoundMap(roundCount);
        return roundMap.get(roundCount);
    }

    public void setRoundInfoClazz(Class<? extends RoundInfo> roundInfoClazz) {
        this.roundInfoClazz = roundInfoClazz;
    }

    public void setCurrentRoundCount(int currentRoundCount) {
        this.currentRoundCount = currentRoundCount;
    }

    public int getCurrentRoundCount() {
        return currentRoundCount;
    }

    /**
     * 创建回合结算实体
     * 
     * @param gameRoleId
     * @author wcy 2017年11月7日
     */
    public void initRoundInfo(String gameRoleId) {
        initRoundMap(currentRoundCount);
        Map<String, RoundInfo> roundInfoMap = roundMap.get(currentRoundCount);
        if (!roundInfoMap.containsKey(gameRoleId)) {
            roundInfoMap.put(gameRoleId, ReflectUtils.newInstance(roundInfoClazz));
        }
    }

    public void initRoundInfo(Collection<String> gameRoleIdCollections) {
        for (String gameRoleId : gameRoleIdCollections) {
            initRoundInfo(gameRoleId);
        }
    }

    private void initRoundMap(int currentRoundCount) {
        if (!roundMap.containsKey(currentRoundCount)) {
            roundMap.put(currentRoundCount, new HashMap<String, RoundInfo>());
        }
    }

    public Map<Integer, Map<String, RoundInfo>> getGameRoundMap() {
        return roundMap;
    }

    /**
     * 
     * @param gameRoleId
     * @return
     * @author wcy 2017年11月7日
     */
    public RoundInfo getRoundInfo(String gameRoleId) {
        return roundMap.get(currentRoundCount).get(gameRoleId);
    }
}
