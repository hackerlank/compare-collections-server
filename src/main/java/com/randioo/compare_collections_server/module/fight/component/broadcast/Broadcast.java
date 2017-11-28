package com.randioo.compare_collections_server.module.fight.component.broadcast;

/**
 * 广播
 *
 * @param <T>
 * @param <V>
 * @author wcy 2017年9月17日
 */
public interface Broadcast<T, V> {
    /**
     * 广播通知
     *
     * @param container
     * @param message
     * @author wcy 2017年9月17日
     */
    public void broadcast(T container, V message);

    public void broadcastBesides(T container, V message, int roleId);
}
