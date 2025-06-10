package com.glmapper.ai.workflow.core.step.impl;

import com.glmapper.ai.workflow.core.step.WorkflowStep;
import com.glmapper.ai.workflow.model.WorkflowRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;

import javax.validation.constraints.Null;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Classname RouterSelectorWorkflowStep
 * @Description AI路由选择器实现WorkflowStep接口，用于根据用户问题选择合适的路由
 *
 * @Date 2025/6/10 16:36
 * @Created by Gepeng18
 */
@Slf4j
public class RouterSelectorWorkflowStep implements WorkflowStep {

    private final ChatClient chatClient;
    private final Map<String, WorkflowStep> stepMap;
    private final String name;

    public RouterSelectorWorkflowStep(ChatClient chatClient, Map<String, WorkflowStep> stepMap, String name) {
        this.chatClient = chatClient;
        this.stepMap = stepMap;
        this.name = name;
    }

    private static final String PROMPT_TEMPLATE = """
            你是一个专业的路由选择器。根据用户的问题，从以下可用的路由中选择最合适的一个：
            可用路由:
            %s \s
            请仅返回最合适的路由的键名，不要包含任何额外解释。例如，如果最合适的路由是"technical"，只需返回"technical"。""";

    /**
     * 执行步骤
     *
     * @param input 输入数据
     * @return 步骤执行结果
     */
    @Null
    @Override
    public Object execute(Object input) {
        if (!(input instanceof WorkflowRequest)) {
            throw new IllegalArgumentException("Input must be of type WorkflowRequest");
        }

        WorkflowRequest request = (WorkflowRequest) input;

        // 构建提示文本
        String routeInfo = stepMap.entrySet().stream()
                .map(entry -> "- " + entry.getKey() + ": " + entry.getValue().name())
                .collect(Collectors.joining("\n"));

        String promptTemplate = String.format(PROMPT_TEMPLATE, routeInfo);

        // 创建并发送提示
        Prompt prompt = new Prompt(
                new SystemMessage(promptTemplate),
                new UserMessage(request.getQuestion())
        );

        String routeKey = chatClient.prompt(prompt).call().content().trim();
        
        // 确保获取到的是有效路由
        if (!stepMap.containsKey(routeKey)) {
            return null;
        }

        return routeKey;
    }

    @Override
    public String name() {
        return name;
    }

}