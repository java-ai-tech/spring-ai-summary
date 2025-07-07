package com.glmapper.ai.evaluator.optimizer.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Chat client configuration for Evaluator Optimizer
 * 
 * @author glmapper
 */
@Configuration
public class ChatClientConfig {

    @Bean
    @Qualifier("generatorChatClient")
    public ChatClient generatorChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient.builder(openAiChatModel).build();
    }

    @Bean
    @Qualifier("evaluatorChatClient")
    public ChatClient evaluatorChatClient(DeepSeekChatModel deepSeekChatModel) {
        return ChatClient.builder(deepSeekChatModel).build();
    }
}