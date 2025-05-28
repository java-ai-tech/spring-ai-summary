package com.glmapper.ai.chat.deepseek.prompts;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.template.st.StTemplateRenderer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Classname PromptTemplateService
 * @Description PromptTemplateService
 * @Date 2025/5/28 14:39
 * @Created by glmapper
 */
@Service
public class PromptTemplateService {

    @Autowired
    private ChatClient deepSeekChatClient;

    /**
     * 使用 Prompt 模板进行聊天
     *
     * @param userInput 用户输入
     */
    public String prompt01(String userInput) {
        Prompt prompt = new Prompt(userInput);
        // can instead of: this.deepSeekChatClient.prompt(userInput).call().content();
        // can instead of: this.deepSeekChatClient.prompt().user(userInput).call().content();
        String answer = this.deepSeekChatClient.prompt(prompt).call().content();
        return answer;
    }

    /**
     * 使用 Prompt 模板进行聊天，使用占位符
     * 这里使用了默认的模版渲染器，能够识别 {} 格式的占位符
     *
     * 注释中的代码是使用 PromptTemplate 的方式来实现同样的功能：
     *
     * PromptTemplate promptTemplate = new PromptTemplate("使用 json 格式输出这段文字： {content}");
     * Prompt prompt = promptTemplate.create(Map.of("content", userInput));
     * this.deepSeekChatClient.prompt(prompt).call().content();
     *
     * @param userInput 用户输入
     */
    public String prompt02(String userInput) {
        return this.deepSeekChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("使用 json 格式输出这段文字： {content}")
                        .param("content", userInput))
                .call()
                .content();
    }

    /**
     * 使用 Prompt 模板进行聊天，使用自定义占位符
     * 这里使用了自定义的模版渲染器，能够识别自定义  <> 格式的占位符
     *
     * 注释中的代码是使用 PromptTemplate 的方式来实现同样的功能：
     *
     * PromptTemplate promptTemplate = PromptTemplate.builder()
     *     .renderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
     *     .template("""
     *             使用 json 格式输出这段文字： <content>.
     *             """)
     *     .build();
     * String prompt = promptTemplate.render(Map.of("content", userInput));
     * this.deepSeekChatClient.prompt(prompt).call().content();
     *
     * @param userInput 用户输入
     */
    public String prompt03(String userInput) {
        return this.deepSeekChatClient.prompt()
                .user(promptUserSpec -> promptUserSpec.text("使用 json 格式输出这段文字： <content>")
                        .param("content", userInput))
                .templateRenderer(StTemplateRenderer.builder().startDelimiterToken('<').endDelimiterToken('>').build())
                .call()
                .content();
    }
}
