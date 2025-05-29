package com.glmapper.ai.tc.tools.function;

import java.util.function.Function;

/**
 * @Classname WeatherService
 * @Description WeatherService
 * @Date 2025/5/29 17:09
 * @Created by glmapper
 */
public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    public WeatherResponse apply(WeatherRequest request) {
        return new WeatherResponse(30.0, Unit.C);
    }
}
