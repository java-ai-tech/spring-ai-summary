package com.glmapper.ai.observability.tracing.tools.function;

import java.util.function.Function;


/**
 * @Classname WeatherService
 * @Description WeatherService
 */
public class WeatherService implements Function<WeatherRequest, WeatherResponse> {

    public WeatherResponse apply(WeatherRequest request) {
        return new WeatherResponse(30.0, Unit.C);
    }
}
