package com.glmapper.ai.workflow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @Classname OpenaiChatClientConfigs
 * @Description 注入 ChatClient
 *
 * @Date 2025/6/10 09:23
 * @Created by Gepeng18
 */
@Configuration
public class OpenaiChatClientConfigs {

    /**
     * 注入ChatClient
     *
     * @param chatModel
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .build();
    }
} 