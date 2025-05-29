package com.glmapper.ai.chat.memory.jdbc.service;

import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Classname StorageService
 * @Description StorageService
 * @Date 2025/5/29 09:29
 * @Created by glmapper
 */
@Service
public class ChatMemoryService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private JdbcChatMemoryRepository chatMemoryRepository;

    private ChatMemory chatMemory;

    @PostConstruct
    public void init() {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)
                .build();
    }

    public String call(String message, String conversationId) {
        UserMessage userMessage = new UserMessage(message);
        this.chatMemory.add(conversationId, userMessage);
        List<Message> messages = chatMemory.get(conversationId);
        ChatResponse response = chatModel.call(new Prompt(messages));
        chatMemory.add(conversationId, response.getResult().getOutput());
        return response.getResult().getOutput().getText();
    }

    /**
     * 模拟一个对话场景，用户先介绍自己的名字，然后询问 AI 自己的名字
     * <p>
     * 这里使用的方式是 Memory in Chat Model
     *
     * @param message        用户输入的消息
     * @param conversationId 对话 ID
     * @return AI 的回答
     */
    public void chat(String message, String conversationId) {
        // First interaction
        call("My name is James Bond", conversationId);
        // Second interaction
        call("What is my name?", conversationId);
    }


    public ChatMemory getChatMemory() {
        return chatMemory;
    }
}
