package com.glmapper.ai.observability.metric.configs;

import com.glmapper.ai.observability.metric.tools.function.WeatherRequest;
import com.glmapper.ai.observability.metric.tools.function.WeatherResponse;
import com.glmapper.ai.observability.metric.tools.function.WeatherService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Description;

import java.util.function.Function;


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
