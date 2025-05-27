package com.glmapper.controller.chat.service;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @Classname MultiModelService
 * @Description MultiModelService
 * @Date 2025/5/23 16:41
 * @Created by glmapper
 */
@Service
public class FluentApiService {

    private static final Logger logger = LoggerFactory.getLogger(FluentApiService.class);

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    //@PostConstruct
    public void init() {
        // 初始化代码
        this.fluentApi("今天是几号？");
    }

    public String fluentApi(String userInput) {
        // 使用流式 API
        String msg1 = this.deepSeekChatClient.prompt().user(userInput).system("你是一个 AI 助手").call().content();
        // 使用流式 API
        Prompt prompt = new Prompt(userInput);
        String msg2 = this.deepSeekChatClient.prompt(prompt).call().content();
        // 使用流式 API
        String msg3 = this.deepSeekChatClient.prompt(userInput).call().content();
        logger.info("msg1: {}, msg2: {}, msg3: {}", msg1, msg2, msg3);
        return msg1 + "\n" + msg2 + "\n" + msg3;
    }
}
