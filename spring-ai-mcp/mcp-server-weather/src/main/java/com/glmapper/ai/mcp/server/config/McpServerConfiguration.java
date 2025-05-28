package com.glmapper.ai.mcp.server.config;

import com.glmapper.ai.mcp.server.service.WeatherServiceServer;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname McpServerConfiguration
 * @Description McpServerConfiguration
 * @Date 2025/4/15 15:50
 * @Created by glmapper
 */
@Configuration
public class McpServerConfiguration {

    @Bean
    public ToolCallbackProvider toolCallbackProvider(WeatherServiceServer weatherServer) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherServer).build();
    }
}
