package com.glmapper.ai.workflow.workflow;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.glmapper.ai.workflow.workflow.model.WorkflowResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * @Classname OrchestratorWorkersWorkflow
 * @Description Orchestrator 用大模型拆解任务，Worker 并行处理，合并结果
 * @Date 2025/6/12 20:19
 * @Created by glmapper
 */

@Component
public class OrchestratorWorkersWorkflow {

    @Autowired
    private ChatClient chatClient;

    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

    public WorkflowResponse process(String taskDescription) {
        try {
            // 1. 用大模型拆解任务
            LlmSubtaskResult subtaskResult = callLlmForSubtasks(taskDescription);
            List<String> subtasks = subtaskResult.subtasks;
            String analysis = subtaskResult.analysis;
            if (subtasks == null || subtasks.isEmpty()) {
                return WorkflowResponse.builder()
                        .success(false)
                        .errorMessage("大模型未能拆解出子任务")
                        .analysis(analysis)
                        .subtasks(subtasks)
                        .build();
            }

            // 2. Workers process subtasks in parallel
            List<String> workerResponses = subtasks.stream()
                    .map(subtask -> CompletableFuture.supplyAsync(() -> workerProcess(subtask)))
                    .collect(Collectors.toList())
                    .stream()
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            // 3. Results are combined into final response
            String combined = String.join("\n", workerResponses);
            return WorkflowResponse.builder()
                    .content(combined)
                    .success(true)
                    .analysis(analysis)
                    .subtasks(subtasks)
                    .workerResponses(workerResponses)
                    .build();
        } catch (Exception e) {
            return WorkflowResponse.builder()
                    .success(false)
                    .errorMessage("Orchestrator/Worker 执行失败: " + e.getMessage())
                    .build();
        }
    }

    // 用大模型拆解任务，返回原始分析和子任务列表
    private LlmSubtaskResult callLlmForSubtasks(String taskDescription) throws Exception {
        List messages = List.of(new SystemMessage("你是一个任务拆解专家。请将用户输入的复杂任务描述拆解为若干可以独立执行的子任务，输出格式为 JSON 数组，每个元素为一个子任务字符串。"), new UserMessage(taskDescription));
        Prompt prompt = new Prompt(messages);
        String modelResult = chatClient.prompt(prompt).call().content();
        // 解析为 List<String>
        List<String> subtasks;
        try {
            subtasks = OBJECT_MAPPER.readValue(modelResult, new TypeReference<List<String>>() {
            });
        } catch (Exception e) {
            // 容错：如果模型返回不是严格 JSON 数组，尝试简单分割
            subtasks = Arrays.stream(modelResult.split("[。,.，\n]"))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toList());
        }
        return new LlmSubtaskResult(modelResult, subtasks);
    }

    // 内部类：封装大模型分析结果
    private record LlmSubtaskResult(String analysis, List<String> subtasks) {
    }

    private String workerProcess(String subtask) {
        try {
            // 你可以根据业务自定义 system prompt
            String systemPrompt = "你是一个高效的AI助手，请认真完成以下子任务：";
            List messages = List.of(new SystemMessage(systemPrompt), new UserMessage(subtask));
            Prompt prompt = new Prompt(messages);
            // 直接用 chatClient 让大模型"执行"子任务
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            // 失败时返回错误信息，便于排查
            return "[Worker Error] " + e.getMessage();
        }
    }
}
