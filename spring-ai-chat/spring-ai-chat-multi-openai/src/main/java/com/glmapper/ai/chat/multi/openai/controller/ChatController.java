package com.glmapper.ai.chat.multi.openai.controller;

import com.glmapper.ai.chat.multi.openai.service.MultiChatClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname ChatController
 * @Description deepseek ChatController
 * @Date 2025/5/28 13:51
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/multi-chat-openai")
public class ChatController {

    @Autowired
    private MultiChatClientService multiChatClientService;

    @RequestMapping("/chat")
    public void chat() {
        this.multiChatClientService.multiClientFlow();
    }
}
