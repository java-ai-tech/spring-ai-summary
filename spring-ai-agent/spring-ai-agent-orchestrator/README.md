
# 基于大模型的 OrchestratorWorkersWorkflow 智能编排

## 一、背景与目标

在智能体（AI Agent）和自动化编排领域，复杂任务往往需要拆解为多个子任务，并由不同的“worker”并行处理。本文将介绍如何基于 Spring AI 和大模型（LLM），实现一个通用的 OrchestratorWorkersWorkflow，自动完成任务拆解、分发与结果汇总，并通过单元测试进行验证和评估。

---

## 二、核心设计思路

### 1. 任务拆解（Orchestration）

- 利用大模型（如 OpenAI、Qwen 等）对用户输入的复杂任务进行智能拆解，输出结构化的子任务列表。
- 拆解 prompt 采用系统提示词，要求模型以 JSON 数组返回所有可独立执行的子任务。

### 2. 子任务处理（Workers）

- 对每个子任务，worker 再次调用大模型，让其“执行”或“回答”该子任务，实现通用型处理。
- 支持并行处理，提升效率。

### 3. 结果汇总

- 汇总所有 worker 的输出，形成最终的 content。
- 同时保留原始拆解、子任务列表、各 worker 输出，便于溯源和分析。

---

## 三、核心代码实现

### 1. WorkflowResponse 结构

```java
@Data
@Builder
public class WorkflowResponse {
    private String analysis;                // 大模型原始拆解输出
    private List<String> subtasks;          // 拆解出的所有子任务
    private List<String> workerResponses;   // 每个 worker 的输出
    private String content;                 // 最终合成内容
    private boolean success;                // 是否成功
    private String errorMessage;            // 错误信息
}
```

### 2. OrchestratorWorkersWorkflow 主要逻辑

```java
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

    // 用大模型拆解任务
    private LlmSubtaskResult callLlmForSubtasks(String taskDescription) throws Exception {
        List messages = List.of(
                new SystemMessage("你是一个任务拆解专家。请将用户输入的复杂任务描述拆解为若干可以独立执行的子任务，输出格式为 JSON 数组，每个元素为一个子任务字符串。"),
                new UserMessage(taskDescription)
        );
        Prompt prompt = new Prompt(messages);
        String modelResult = chatClient.prompt(prompt).call().content();
        List<String> subtasks;
        try {
            subtasks = OBJECT_MAPPER.readValue(modelResult, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            subtasks = Arrays.stream(modelResult.split("[。,.，\n]")).map(String::trim).filter(s -> !s.isEmpty()).collect(Collectors.toList());
        }
        return new LlmSubtaskResult(modelResult, subtasks);
    }

    // 通用 worker 处理逻辑：再次用大模型处理子任务
    private String workerProcess(String subtask) {
        try {
            String systemPrompt = "你是一个高效的AI助手，请认真完成以下子任务：";
            List messages = List.of(
                    new SystemMessage(systemPrompt),
                    new UserMessage(subtask)
            );
            Prompt prompt = new Prompt(messages);
            return chatClient.prompt(prompt).call().content();
        } catch (Exception e) {
            return "[Worker Error] " + e.getMessage();
        }
    }

    // 内部类：封装大模型分析结果
    private static class LlmSubtaskResult {
        final String analysis;
        final List<String> subtasks;
        LlmSubtaskResult(String analysis, List<String> subtasks) {
            this.analysis = analysis;
            this.subtasks = subtasks;
        }
    }
}
```

---

## 四、测试用例与验证

### 1. 测试用例设计

- 输入任务描述："Generate both technical and user-friendly documentation for a REST API endpoint"
- 期望：大模型能拆解出合理的文档编写子任务，worker 能对每个子任务给出专业输出。

### 2. 测试结果（test_result.md 摘要）

- **Analysis**（大模型拆解原始输出）：
  ```json
  [
    "Identify the REST API endpoint to be documented",
    "Gather technical specifications of the endpoint (e.g., HTTP method, URL, request/response formats)",
    "Document the endpoint's purpose and functionality",
    ...
    "Format the documentation for readability and consistency"
  ]
  ```
- **Worker Outputs**（每个子任务的处理结果）：
    - 针对“Identify the REST API endpoint to be documented”，worker 会主动询问 API 细节，体现了智能 agent 的交互性。
    - 针对“Gather technical specifications...”，worker 会列举常见的技术规格项，并给出格式建议。
    - 针对“Write clear, step-by-step usage instructions for developers”，worker 会输出结构化的开发者指引。
    - 其他子任务也都能得到合理、专业的响应。

- **最终 content**：为所有 worker 输出的合成文本，便于直接展示或下游处理。

---

## 五、结果评估与客观分析

### 1. 优点

- **高度自动化**：无需手写拆解规则，完全依赖大模型智能分析。
- **通用性强**：worker 逻辑可适配各种类型的子任务，适合多场景复用。
- **可扩展性好**：支持并行处理，易于横向扩展。
- **结构化输出**：便于前端展示、流程追踪和后续分析。

### 2. 局限与改进空间

- **大模型输出稳定性**：部分模型可能输出非严格 JSON，需做好容错处理。
- **worker 处理深度**：如需更专业的子任务处理，可为不同类型子任务定制专属 worker。
- **性能优化**：大规模并发时可引入线程池、限流等机制。

### 3. 适用场景

- 智能文档生成、API 文档自动化
- 复杂任务的自动拆解与执行
- AI Agent、RPA、智能问答等

---

## 六、结语

通过 OrchestratorWorkersWorkflow 的实现与验证，我们可以看到大模型驱动的智能编排在实际业务中的巨大潜力。只需简单 prompt，即可实现复杂任务的自动拆解与高效执行。未来，结合更细粒度的 worker 能力和更强的模型，智能体编排将更加智能和实用。