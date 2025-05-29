package com.glmapper.ai.tc;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname ToolCallingTests
 * @Description tool calling 测试
 * @Date 2025/5/29 15:40
 * @Created by glmapper
 */
@SpringBootTest(classes = ToolCallingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ToolCallingTests {

    @Autowired
    private ChatClient chatClient;

    @Test
    public void testToolCalling() {
        String answer = this.chatClient.prompt().user("今天是几号").call().content();
        System.out.println("AI回答: " + answer);
    }

    @Test
    public void testToolCallingWithParams() {
        String answer = this.chatClient.prompt()
                .user("帮我读取 spring-ai-tool-calling 模块下 application.properties 文件的内容, 文件路径为：/Users/glmapper/Documents/projects/glmapper/spring-ai-summary/spring-ai-tool-calling/src/main/resources/application.properties")
                .call()
                .content();
        System.out.println("AI回答: " + answer);
    }
}
