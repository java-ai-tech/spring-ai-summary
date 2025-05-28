package com.glmapper.ai.mcp.client.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 聊天控制器，处理AI聊天请求
 */
@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    @Resource
    private ChatClient chatClient;

    @GetMapping("/ai")
    String generation(String userInput) {
        return this.chatClient.prompt().user(userInput).call().content();
    }
} 