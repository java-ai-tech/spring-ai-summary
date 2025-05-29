package com.glmapper.ai.tc.controlled;

import com.glmapper.ai.tc.tools.methods.DateTimeTools;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * @Classname UserControlledExecutor
 * @Description UserControlledExecutor
 * @Date 2025/5/29 20:42
 * @Created by glmapper
 */
@Component
public class UserControlledExecutor {

    @Autowired
    private ChatModel chatModel;

    public String manualExecTools(String message) {
        // 创建一个 ToolCallingManager 实例
        ToolCallingManager toolCallingManager = ToolCallingManager.builder().build();
        // 注册工具方法
        ToolCallback[] toolCallbacks = ToolCallbacks.from(new DateTimeTools());
        // 创建一个 ChatOptions 实例，包含工具调用选项
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(toolCallbacks)
                .internalToolExecutionEnabled(false)
                .build();
        // 创建一个 Prompt 实例，包含用户消息和工具调用选项
        Prompt prompt = new Prompt(message, chatOptions);
        // 调用 ChatModel 进行对话
        ChatResponse chatResponse = chatModel.call(prompt);
        // 如果 ChatResponse 包含工具调用，则执行工具调用
        while (chatResponse.hasToolCalls()) {
            // 执行工具调用
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(prompt, chatResponse);
            // 更新 Prompt 实例，包含工具执行结果和工具调用选项
            prompt = new Prompt(toolExecutionResult.conversationHistory(), chatOptions);
            // 再次调用 ChatModel 进行对话
            chatResponse = chatModel.call(prompt);
        }
        // 获取最终的回答
        String answer = chatResponse.getResult().getOutput().getText();
        System.out.println(answer);
        return answer;
    }
}
