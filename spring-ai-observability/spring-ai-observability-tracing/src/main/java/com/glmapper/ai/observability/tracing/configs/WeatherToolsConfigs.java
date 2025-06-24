package com.glmapper.ai.observability.tracing.configs;

import com.glmapper.ai.observability.tracing.tools.function.WeatherRequest;
import com.glmapper.ai.observability.tracing.tools.function.WeatherResponse;
import com.glmapper.ai.observability.tracing.tools.function.WeatherService;
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
