package com.glmapper.ai.nacos.client.controller;

import jakarta.annotation.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/mcp/chat")
@RequiredArgsConstructor
public class ChatController {

    @Resource
    private ChatClient chatClient;

    @GetMapping("/ai")
    public String generation(String userInput) {
        return this.chatClient.prompt().user(userInput).call().content();
    }

}