package com.glmapper.ai.chat.multi.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname ChatController
 * @Description deepseek ChatController
 * @Date 2025/5/28 13:51
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/multi-chat")
public class ChatController {

    @Autowired
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @RequestMapping("/chat")
    public void chat() {

    }
}
