package com.randioo.compare_collections_server.module.match.component;

/**
 * @author zsy
 * @Description:
 * @create 2017-11-28 14:58
 **/
public class MatchInfo {
    public MatchInfo(int roleId, int matchParameter) {
        this.roleId = roleId;
        this.matchParameter = matchParameter;
    }

    public int roleId;
    public int matchParameter;
}
