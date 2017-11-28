package com.randioo.compare_collections_server.module.fight.component.rule;

/**
 * 重连器
 * 
 * @author wcy 2017年11月20日
 *
 * @param <T>
 */
public interface IReconnector<T, R, V> {
    public V getReconnectData(T game, R role);
}
