package com.glmapper.ai.mcp.server;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Hello world!
 */
@SpringBootApplication
public class WeatherMcpServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(WeatherMcpServerApplication.class, args);
    }


    @Autowired
    private RequestMappingHandlerMapping requestMappingHandlerMapping;

    @PostConstruct
    public void showEndpoints() {
        Map<String, Object> data =  requestMappingHandlerMapping.getHandlerMethods().entrySet().stream()
                .collect(Collectors.toMap(
                        e -> e.getKey().toString(),
                        e -> e.getValue().toString()
                ));

        //  遍历输出
        data.entrySet().stream()
                .forEach(entry -> {
                    String key = entry.getKey();
                    Object value = entry.getValue();
                    System.out.println(key + " : " + value);
                });
    }
}
