package com.glmapper.ai.nacos.client.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class McpClientConfig {
    @Bean
    ChatClient chatClient(OpenAiChatModel chatModel, @Qualifier("loadbalancedMcpAsyncToolCallbacks") ToolCallbackProvider toolCallbackProvider) {
        return ChatClient.builder(chatModel).defaultToolCallbacks(toolCallbackProvider.getToolCallbacks()).build();
    }
}