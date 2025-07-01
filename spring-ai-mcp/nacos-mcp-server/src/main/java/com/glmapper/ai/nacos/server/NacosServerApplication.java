package com.glmapper.ai.nacos.server;

import com.glmapper.ai.nacos.server.service.WeatherService;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class NacosServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(NacosServerApplication.class, args);
    }

    // 注册工具回调
    @Bean
    public ToolCallbackProvider weatherTools(WeatherService weatherService) {
        return MethodToolCallbackProvider.builder().toolObjects(weatherService).build();
    }

} 