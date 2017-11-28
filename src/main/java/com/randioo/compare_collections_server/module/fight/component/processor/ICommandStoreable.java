package com.randioo.compare_collections_server.module.fight.component.processor;

import java.util.Stack;

/**
 * 命令存储接口
 * 
 * @author wcy 2017年10月28日
 *
 */
public interface ICommandStoreable {

	/**
	 * 
	 * @return
	 * @author wcy 2017年8月25日
	 */
    public Stack<String> getCmdStack();
}
