package com.glmapper.controller.chat;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * Using an autoconfigured ChatClient.Builder
 */
@RestController
@RequestMapping("/api/chat-prompt-template")
public class ChatPromptTemplateController {

    @Autowired
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;

    /**
     * 使用默认的模板渲染器值
     *
     * @param userInput
     * @return
     */
    @GetMapping("/chatPromptTemplates")
    public String chatPromptTemplates(@RequestParam String userInput) {
        return this.openAiChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("使用 json 格式输出这段文字： {content}")
                        .param("content", userInput))
                .call()
                .content();
    }

    /**
     * 使用自定义模板渲染器
     *
     * @param userInput
     * @return
     */
    @GetMapping("/chatCustomPromptTemplates")
    public String chatPromptTemplates2(@RequestParam String userInput) {
        String answer = openAiChatClient.prompt()
                .user(u -> u.text("Tell me the names of 5 movies whose soundtrack was composed by <composer>")
                        .param("composer", userInput))
                .templateRenderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .call()
                .content();
        return answer;
    }
}
