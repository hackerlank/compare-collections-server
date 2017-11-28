package com.randioo.compare_collections_server.module.fight.component.processor;

import java.util.List;

/**
 * 命令回调函数
 * 
 * @author wcy 2017年10月28日
 *
 * @param <T>
 */
public interface ICommandCallback<T> {
    /**
     * 
     * @param majiangState
     * @return
     * @author wcy 2017年8月21日
     */
    public List<String> afterCommandExecute(T game, String flowName, String[] params);
}
