package com.glmapper.ai.chat.memory.local.controller;

import com.glmapper.ai.chat.memory.local.service.ChatMemoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname TestController
 * @Description
 * <p>
 *     # 你好，我是glmapper
 *     1、curl -X GET http://localhost:8083/api/test/chat?message=%E4%BD%A0%E5%A5%BD%EF%BC%8C%E6%88%91%E6%98%AFglmapper&conversationId=test-1101
 *     # 我是谁？
 *     2、curl -X GET http://localhost:8083/api/test/chat?message=%E6%88%91%E6%98%AF%E8%B0%81%EF%BC%9F&conversationId=test-1101
 *
 * </p>
 * @Date 2025/6/14 17:36
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/test")
public class ChatMemoryController {

    @Autowired
    private ChatMemoryService chatMemoryService;

    @RequestMapping("chat")
    public String test(String message, String conversationId) {
        return this.chatMemoryService.chat(message, conversationId);
    }
}
