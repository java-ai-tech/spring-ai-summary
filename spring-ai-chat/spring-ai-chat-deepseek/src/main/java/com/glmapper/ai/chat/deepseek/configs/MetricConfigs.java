package com.glmapper.ai.chat.deepseek.configs;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Metrics;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname MetricService
 * @Description MetricService
 * @Date 2025/5/27 13:54
 * @Created by glmapper
 */
@Configuration
public class MetricConfigs {

    @Autowired
    private MeterRegistry registry;

    /**
     * 在应用启动时注册 MeterRegistry
     */
    @PostConstruct
    public void init() {
        Metrics.addRegistry(registry);
    }
}
