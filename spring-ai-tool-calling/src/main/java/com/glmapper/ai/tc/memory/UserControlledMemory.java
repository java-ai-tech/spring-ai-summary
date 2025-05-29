package com.glmapper.ai.tc.memory;

import com.glmapper.ai.tc.tools.methods.MathTools;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.DefaultToolCallingManager;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolCallingManager;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * @Classname UserControlledMemory
 * @Description 自定义控制工具调用 + chat memory
 * @Date 2025/5/29 20:51
 * @Created by glmapper
 */
@Component
public class UserControlledMemory {

    /**
     * 默认是基于内存的对话记忆
     */
    @Autowired
    private ChatMemory chatMemory;

    @Autowired
    private ChatModel chatModel;

    private String chatWithTools(String message, String conversationId) {
        ToolCallingManager toolCallingManager = DefaultToolCallingManager.builder().build();
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(ToolCallbacks.from(new MathTools()))
                .internalToolExecutionEnabled(false)
                .build();
        Prompt prompt = new Prompt(List.of(new SystemMessage("You are a helpful assistant."), new UserMessage(message)), chatOptions);
        chatMemory.add(conversationId, prompt.getInstructions());

        Prompt promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
        ChatResponse chatResponse = chatModel.call(promptWithMemory);
        chatMemory.add(conversationId, chatResponse.getResult().getOutput());

        while (chatResponse.hasToolCalls()) {
            ToolExecutionResult toolExecutionResult = toolCallingManager.executeToolCalls(promptWithMemory, chatResponse);
            chatMemory.add(conversationId, toolExecutionResult.conversationHistory()
                    .get(toolExecutionResult.conversationHistory().size() - 1));
            promptWithMemory = new Prompt(chatMemory.get(conversationId), chatOptions);
            chatResponse = chatModel.call(promptWithMemory);
            chatMemory.add(conversationId, chatResponse.getResult().getOutput());
        }
        return chatResponse.getResult().getOutput().getText();
    }


    private String chat(String message, String conversationId) {
        UserMessage newUserMessage = new UserMessage(message);
        chatMemory.add(conversationId, newUserMessage);
        ChatResponse newResponse = chatModel.call(new Prompt(chatMemory.get(conversationId)));
        return newResponse.getResult().getOutput().getText();
    }

    public String testChat(String message) {
        String conversationId = UUID.randomUUID().toString();
        String answer = chatWithTools("What is 6 * 8?", conversationId);
        System.out.println(answer);
        answer = chat("What did I ask you earlier?", conversationId);
        System.out.println(answer);

        return answer;
    }
}
