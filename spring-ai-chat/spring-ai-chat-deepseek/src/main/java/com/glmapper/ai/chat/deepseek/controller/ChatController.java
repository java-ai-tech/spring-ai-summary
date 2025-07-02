package com.glmapper.ai.chat.deepseek.controller;

import com.glmapper.ai.chat.deepseek.advisors.SimpleMetricAdvisor;
import com.glmapper.ai.chat.deepseek.prompts.PromptTemplateService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname ChatController
 * @Description deepseek ChatController
 * @Date 2025/5/28 13:51
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/deepseek")
public class ChatController {

    @Autowired
    private ChatClient deepSeekChatClient;

    @Autowired
    private PromptTemplateService promptTemplateService;

    /**
     * 普通的聊天接口
     *
     * @param userInput 用户输入
     * @return 返回内容
     */
    @GetMapping("/chatWithPrompt")
    public String prompt(@RequestParam String userInput, @RequestParam(required = false, defaultValue = "01") String promptType) {
        if ("01".equals(promptType)) {
            return this.promptTemplateService.prompt01(userInput);
        } else if ("02".equals(promptType)) {
            return this.promptTemplateService.prompt02(userInput);
        } else if ("03".equals(promptType)) {
            return this.promptTemplateService.prompt03(userInput);
        }
        return "Unsupported prompt type: " + promptType;
    }

    /**
     * 会统计 token 使用情况的聊天接口
     *
     * @param userInput 用户输入
     * @return 返回内容
     */
    @GetMapping("/chatWithMetric")
    public String chatWithMetric(@RequestParam String userInput) {
        SimpleMetricAdvisor metricAdvisor = new SimpleMetricAdvisor();
        return this.deepSeekChatClient.prompt().advisors(metricAdvisor).user(userInput).call().content();
    }
}
