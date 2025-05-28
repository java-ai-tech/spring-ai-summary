package com.glmapper.ai.chat.deepseek.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
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
public class DeepseekChatClientConfigs {

    @Bean
    public ChatClient deepSeekChatClient(DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
                .build();
    }
}
