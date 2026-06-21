package com.glmapper.ai.chat.ollama.util;

/**
 * @description：
 * @author：dxs
 * @date：2026/6/21 12:24
 *
 */
public class TestUtils {
    // 编写一个性能很差的方法
    public static void slowMethod() {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
