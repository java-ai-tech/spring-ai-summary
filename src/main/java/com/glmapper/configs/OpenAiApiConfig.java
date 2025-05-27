package com.glmapper.configs;

import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiApiConfig {
    
    @Bean
    public OpenAiApi baseOpenAiApi() {
        return OpenAiApi.builder()
                .apiKey(System.getenv("DEEPSEEK_API_KEY"))
                .build();
    }
} 