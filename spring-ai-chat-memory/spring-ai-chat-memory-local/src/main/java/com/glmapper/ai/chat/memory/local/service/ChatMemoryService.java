package com.glmapper.ai.chat.memory.local.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname ChatMemoryService
 * @Description ChatMemoryService
 * @Date 2025/5/28 17:21
 * @Created by glmapper
 */
@Service
public class ChatMemoryService {

    private static final String CONVERSATION_ID = "naming-20250528";

    @Autowired
    private ChatClient chatClient;

    public String chat(String message, String conversationId) {
        String answer = this.chatClient.prompt()
                .user(message)
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId == null ? CONVERSATION_ID : conversationId))
                .call()
                .content();
        return answer;
    }
}
