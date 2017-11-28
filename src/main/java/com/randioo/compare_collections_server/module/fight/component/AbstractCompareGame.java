package com.randioo.compare_collections_server.module.fight.component;

import java.util.Stack;

import org.slf4j.Logger;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.processor.ICommandStoreable;
import com.randioo.compare_collections_server.module.fight.component.processor.ICompareGameRule;

public class AbstractCompareGame implements ICommandStoreable {
    public Logger logger;
    private Stack<String> cmdStack = new Stack<>();
    private ICompareGameRule<Game> rule;

    @Override
    public Stack<String> getCmdStack() {
        return cmdStack;
    }

    public ICompareGameRule<Game> getRule() {
        return rule;
    }

    public void setRule(ICompareGameRule<Game> rule) {
        this.rule = rule;
    }
}
