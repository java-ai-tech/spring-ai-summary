package com.glmapper.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In all cases, you need to disable the ChatClient.Builder autoconfiguration by setting the property
 * > spring.ai.chat.client.enabled=false.
 *
 * @Classname ChatClientConfigs
 * @Description ChatClientConfigs
 * @Date 2025/4/15 14:53
 * @Created by glmapper
 */
@Configuration
@ConditionalOnProperty(name = "spring.ai.chat.client.enabled", havingValue = "false")
public class MultiChatClientConfigs {
    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean
    public ChatClient deepSeekChatClient(DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a friendly chat bot that answers question with json always")
                .build();
    }
}
