package com.glmapper.controller.chat;

import com.glmapper.advisors.SimpleMetricAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.prompt.Prompt;
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
@RequestMapping("/api/chat")
public class ChatController {

    @Autowired
    @Qualifier("openAiChatClient")
    private ChatClient openAiChatClient;

    @Autowired
    @Qualifier("deepSeekChatClient")
    private ChatClient deepSeekChatClient;

    @GetMapping("/prompt")
    public String prompt(@RequestParam String userInput) {
        SimpleMetricAdvisor customMetric = new SimpleMetricAdvisor();
        // 返回的数据都是 json 格式
        return this.deepSeekChatClient.prompt().advisors(customMetric).user(userInput).call().content();
    }


    @GetMapping("/chatPrompt")
    public String chatPrompt(@RequestParam String userInput) {
        SimpleLoggerAdvisor customLogger = new SimpleLoggerAdvisor();
        Prompt prompt = new Prompt(userInput);
        return this.openAiChatClient.prompt(prompt).advisors(customLogger).call().content();
    }
}
