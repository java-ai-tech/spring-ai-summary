package com.glmapper.ai.chat.doubao.configs;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname ChatClientConfigs
 * @Description ChatClientConfigs
 * @Date 2025/4/15 14:53
 * @Created by glmapper
 */
@Configuration
public class DoubaoChatClientConfigs {
    /**
     * 1、这里使用的是 openai 协议的 ChatModel，因此这里动态注入的 ChatModel 是 OpenAiChatModel
     * 与 spring-ai-chat-deepseek 模块的 DeepSeekChatModel 不同
     * <p>
     * 2、这里 chatClient 设置了默认的系统提示语，会将所有的聊天请求都带上这个系统提示语，返回的内容均为 JSON 格式
     *
     * @param chatModel
     * @return ChatClient
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
