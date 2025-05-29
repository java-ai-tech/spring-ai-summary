package com.glmapper.ai.tc.tools.methods;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @Classname MathTools
 * @Description TODO
 * @Date 2025/5/29 20:55
 * @Created by glmapper
 */
public class MathTools {

    /**
     * 计算两个数的和
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两个数的和
     */
    @Tool(description = "计算两个数的和")
    public int add(int a, int b) {
        return a + b;
    }

    /**
     * 计算两个数的差
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两个数的差
     */
    @Tool(description = "计算两个数的差")
    public int subtract(int a, int b) {
        return a - b;
    }

    /**
     * 计算两个数的积
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两个数的积
     */
    @Tool(description = "计算两个数的积")
    public int multiply(int a, int b) {
        return a * b;
    }

    /**
     * 计算两个数的商
     *
     * @param a 第一个数
     * @param b 第二个数
     * @return 两个数的商
     */
    @Tool(description = "计算两个数的商")
    public double divide(int a, int b) {
        if (b == 0) {
            throw new IllegalArgumentException("除数不能为零");
        }
        return (double) a / b;
    }
}
