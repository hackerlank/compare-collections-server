package com.randioo.compare_collections_server.module.fight.component.processor;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.randioo.compare_collections_server.entity.po.Game;
import com.randioo.compare_collections_server.module.fight.component.Flow;
import com.randioo.randioo_server_base.utils.PackageUtil;

/**
 * 流程控制器
 *
 * @author wcy 2017年8月24日
 */
public class Processor implements ApplicationContextAware {
    private static final Logger logger = LoggerFactory.getLogger(Processor.class);

    private Map<String, Flow> flows = new HashMap<>();

    private String basePackage;

    public void setBasePackage(String path) {
        this.basePackage = path;
    }

    @Autowired
    private FlowCommandConverter flowCommandConverter;

    @Override
    public void setApplicationContext(ApplicationContext context) throws BeansException {
        List<Class<?>> classes = PackageUtil.getClasses(basePackage);
        for (Class<?> clazz : classes) {
            logger.debug("load flow : {}", clazz);
            String flowName = clazz.getSimpleName();
            Flow flow = (Flow) context.getBean(clazz);
            flows.put(flowName, flow);
        }
    }

    /**
     * 执行下一个动作
     *
     * @param game
     * @author wcy 2017年8月23日
     */
    public void process(Game game) {
        // 继续执行执行出栈
        Stack<String> cmdStack = game.getCmdStack();
        // 栈顶为等待操作状态时不继续流程
        while (cmdStack.size() != 0 && !cmdStack.peek().equals("wait")) {
            ICompareGameRule<Game> rule = game.getRule();

            String topOperation = cmdStack.pop();
            logger.info("pop process={}", topOperation);

            FlowCommand flowCommand = flowCommandConverter.parse(topOperation);
            // 获得当前事件
            Flow flow = flows.get(flowCommand.getFlowName());

            if (flow != null) {
                // 执行流过程
                flow.execute(game, flowCommand.getParams());
            } else {
                this.noFlowException(flowCommand.getFlowName());
            }

            // 游戏结束标识,直接结束
            if (topOperation.equals("FlowGameOver")) {
                break;
            }

            List<String> list = rule.afterCommandExecute(game, flowCommand.getFlowName(), flowCommand.getParams());
            this.addProcesses(cmdStack, list);
            logger.info("add process= {}", list);
            logger.info("remain process {}", cmdStack);

        }

    }

    public void pop(Game game) {
        if (game.getCmdStack().size() > 0) {
            game.getCmdStack().pop();
        } else {
            game.logger.error("栈已空不可弹出");
        }
    }

    public void push(Game game, List<String> states) {
        for (String item : states) {
            game.getCmdStack().push(item);
        }
    }

    public void push(Game game, String... states) {
        for (String item : states) {
            game.getCmdStack().push(item);
        }
    }

    /**
     * 添加流程 例子：<br>
     * p1,p2,p3->p3,p2,p1<br>
     *
     * @param stack
     * @param list
     * @author wcy 2017年8月25日
     */
    private void addProcesses(Stack<String> stack, List<String> list) {
        if (list == null || list.size() == 0) {
            return;
        }

        for (int i = list.size() - 1; i >= 0; i--) {
            String state = list.get(i);
            stack.push(state);
        }
    }

    public void nextProcess(Game game, String flowName) {
        pop(game);
        push(game, flowName);

        process(game);
    }

    private void noFlowException(String stateEnum) {
        String equals = "==========================================================";
        String context = " no flow : " + stateEnum + " ";
        int len1 = equals.length();
        int len2 = context.length();

        StringBuilder sb = new StringBuilder();
        sb.append("\n");
        sb.append(equals).append("\n");
        for (int i = 0; i < (len1 - len2) / 2; i++) {
            sb.append("=");
        }
        sb.append(context);
        for (int i = 0; i < (len1 - len2) / 2; i++) {
            sb.append("=");
        }
        sb.append("\n");
        sb.append(equals).append("\n");
        logger.info(sb.toString());
    }

}
