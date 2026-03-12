package com.glmapper.agent.react.configs;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.ClientHttpRequestFactorySettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * @Classname HTTPConfig
 * @Description TODO
 * @Date 2/6/26 3:34 PM
 * @Created by glmapper
 */
@Configuration
public class HTTPConfig {

    @Value("${spring.http.client.connect-timeout:60s}")
    private Duration connectTimeout;

    @Value("${spring.http.client.read-timeout:60s}")
    private Duration readTimeout;

    @Bean
    public ClientHttpRequestFactorySettings clientHttpRequestFactorySettings() {
        return ClientHttpRequestFactorySettings.DEFAULTS.withConnectTimeout(connectTimeout)
                .withReadTimeout(readTimeout);
    }
}
