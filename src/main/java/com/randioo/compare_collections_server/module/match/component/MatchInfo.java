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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MatchInfo)) return false;

        MatchInfo matchInfo = (MatchInfo) o;

        return roleId == matchInfo.roleId;
    }

    @Override
    public int hashCode() {
        return roleId;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("MatchInfo{");
        sb.append("roleId=").append(roleId);
        sb.append(", matchParameter=").append(matchParameter);
        sb.append('}');
        return sb.toString();
    }
}
