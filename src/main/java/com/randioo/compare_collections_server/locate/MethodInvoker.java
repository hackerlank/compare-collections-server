package com.randioo.compare_collections_server.locate;

import java.util.Arrays;

/**
 * 方法调用器
 * 
 * @author wcy 2017年10月10日
 *
 */
public class MethodInvoker {
    public String methodName;
    public Object[] args;

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("MethodInvoker [methodName=").append(methodName).append(", args=").append(Arrays.toString(args))
                .append("]");
        return builder.toString();
    }

}
