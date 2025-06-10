package com.glmapper.ai.workflow.core.step.impl;

import com.glmapper.ai.workflow.core.step.WorkflowStep;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;


/**
 * @Classname DefaultWorkflowStep
 * @Description 工作流步骤
 *
 * @Date 2025/6/10 17:23
 * @Created by Gepeng18
 */
public record DefaultWorkflowStep(String name, String promptTemplate, ChatClient chatClient) implements WorkflowStep {

    /**
     * 执行本步骤
     *
     * @param input 输入数据
     * @return 步骤执行结果
     */
    @Override
    public Object execute(Object input) {
        String inputStr = input != null ? input.toString() : "";
        Prompt prompt = new Prompt(
                new SystemMessage(promptTemplate),
                new UserMessage(inputStr)
        );

        return chatClient.prompt(prompt).call().content();
    }
}