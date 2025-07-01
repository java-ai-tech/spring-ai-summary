package com.glmapper.ai.nacos.server.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class WeatherService {

    @Tool(description = "查询指定城市当前的天气")
    public String getCurrentWeather(@ToolParam(description = "城市名称") String cityName) {
        log.info("当前城市：{}", cityName);
        return "天气大风";
    }

}