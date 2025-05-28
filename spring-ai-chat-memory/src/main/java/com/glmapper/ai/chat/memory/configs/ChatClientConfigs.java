package com.glmapper.ai.chat.memory.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * In all cases, you need to disable the ChatClient.Builder autoconfiguration by setting the property
 *
 * @Classname ChatClientConfigs
 * @Description ChatClientConfigs
 * @Date 2025/4/15 14:53
 * @Created by glmapper
 */
@Configuration
public class ChatClientConfigs {

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
                .build();
    }
}
