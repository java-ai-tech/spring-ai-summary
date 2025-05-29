package com.glmapper.ai.tc.configs;

import com.glmapper.ai.tc.tools.function.WeatherRequest;
import com.glmapper.ai.tc.tools.function.WeatherResponse;
import com.glmapper.ai.tc.tools.function.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;

/**
 *
 *  使用方式：
 * ChatClient chatClient = ChatClient.builder(chatModel)
 *                 .defaultTools("currentWeather")
 *                 .build();
 *
 *
 *
 * @Classname WeatherTools
 * @Description WeatherTools
 * @Date 2025/5/29 17:16
 * @Created by glmapper
 */
@Configuration(proxyBeanMethods = false)
public class WeatherToolsConfigs {
    public static final String CURRENT_WEATHER_TOOL = "currentWeather";

    WeatherService weatherService = new WeatherService();

    @Bean(CURRENT_WEATHER_TOOL)
    @Description("Get the weather in location")
    Function<WeatherRequest, WeatherResponse> currentWeather() {
        return weatherService;
    }

}
