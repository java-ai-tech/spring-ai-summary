package com.glmapper.ai.chat.openai.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname ChatClientConfigs
 * @Description ChatClientConfigs
 * @Date 2025/4/15 14:53
 * @Created by glmapper
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.chat.client.enabled", havingValue = "false", matchIfMissing = true)
public class OpenaiChatClientConfigs {
    /**
     * 注入ChatClient
     *
     * @param chatModel
     * @return
     */
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        //return ChatClient.builder(chatModel).build();
        // build with Default System Text
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a friendly chat bot that answers question with json always")
                .build();
    }
}
