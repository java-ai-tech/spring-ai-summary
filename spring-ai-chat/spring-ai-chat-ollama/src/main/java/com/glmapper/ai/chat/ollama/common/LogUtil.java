package com.glmapper.ai.chat.ollama.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {
    
    private static final Logger log = LoggerFactory.getLogger(LogUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 记录请求参数
     * @param methodName 方法名
     * @param params 参数
     */
    public static void logRequest(String methodName, Object[] params) {
        try {
            String paramsJson = params != null ? objectMapper.writeValueAsString(params) : "null";
            log.info("""
                    
                    === Request ===
                    Method: %s
                    Parameters: %s
                    """, methodName, paramsJson);
        } catch (Exception e) {
            log.error("Error logging request", e);
        }
    }

    /**
     * 记录响应结果
     * @param methodName 方法名
     * @param result 响应结果
     */
    public static void logResponse(String methodName, Object result) {
        try {
            String resultJson = result != null ? objectMapper.writeValueAsString(result) : "null";
            log.info("""
                    
                    === Response ===
                    Method: %s
                    Result: %s
                    """, methodName, resultJson);
        } catch (Exception e) {
            log.error("Error logging response", e);
        }
    }
}