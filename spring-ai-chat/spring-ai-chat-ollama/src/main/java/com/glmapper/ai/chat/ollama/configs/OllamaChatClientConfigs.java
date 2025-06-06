package com.glmapper.ai.chat.ollama.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname OllamaChatClientConfigs
 * @Description OllamaChatClientConfigs
 * @Date 2025/6/6 08:17
 * @Created by Gepeng18
 */
@Configuration
public class OllamaChatClientConfigs {
    /**
     * 1、这里使用的是 Ollama 协议的 ChatModel，因此这里动态注入的 ChatModel 是 OllamaChatModel
     * <p>
     * 2、这里 chatClient 设置了默认的系统提示语，会将所有的聊天请求都带上这个系统提示语，返回的内容均为 JSON 格式
     *
     * @param chatModel
     * @return ChatClient
     */
    @Bean
    public ChatClient chatClient(OllamaChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a friendly chat bot that answers question with json always")
                .build();
    }
}