## 模型评估
在测试 AI 应用时，需要对生成的内容进行评估，以避免模型输出“幻觉”信息（即虚构、不准确的内容）。一种常见的评估方式是直接借助 AI 模型本身来判断答案的质量。此时应选用一个更适合评估任务的模型，它未必和用于生成答案的模型相同。在 Spring AI 中，专门用于结果评估的接口是 `Evaluator`，它的定义如下：

```java
@FunctionalInterface
public interface Evaluator {
    EvaluationResponse evaluate(EvaluationRequest evaluationRequest);
}
```

评估时传入的是一个特定的请求类型 EvaluationRequest，它包括几个关键的属性，如下：

```java
public class EvaluationRequest {
    // 用户输入的原始问题
	private final String userText;
    // 上下文信息，可以是文档，历史对话等等
	private final List<Content> dataList;
    // 模型针对用户问题返回的结果
	private final String responseContent;
    
	public EvaluationRequest(String userText, List<Content> dataList, String responseContent) {
		this.userText = userText;
		this.dataList = dataList;
		this.responseContent = responseContent;
	}
  ...
}
```



# 关联评估
`RelevancyEvaluator` 是对 `Evaluator` 接口的具体实现，主要用于判断 AI 所生成的回答是否与检索到的上下文信息相关联。这项评估机制常被用来检测 RAG（检索增强生成）流程中的响应质量，确保模型的回复能贴合用户提问及相关内容。评估过程依赖三个要素：**用户输入、AI 模型的回答**，**以及检索得到的上下文内容**。系统会通过提示词模板向 AI 发出提问，然后判断模型回答是否具有上下文相关性。下面是 `RelevancyEvaluator` 默认使用的提示模板内容：

```java
Your task is to evaluate if the response for the query
is in line with the context information provided.

You have two options to answer. Either YES or NO.

Answer YES, if the response for the query
is in line with context information otherwise NO.

Query:
{query}

Response:
{response}

Context:
{context}

Answer:
```



## 案例介绍
这里我们使用 deepseek 来评估豆包的输出结果，关于如何在项目中使用多个 chatmodel 可以参考 chat client api 部分。

```java
/**
 * 评估消息内容
 *
 * @param message
 * @return
 */
public EvaluationResponse evaluate(String message) {
    // 使用 OpenAI 模型进行评估
    String openAiResponse = openAiChatClient.prompt().user(message).call().content();
    String question = message;
    EvaluationRequest evaluationRequest = new EvaluationRequest(
            // The original user question
            question,
            // context data
            Collections.emptyList(),
            // The AI model's response
            openAiResponse);
    RelevancyEvaluator evaluator = new RelevancyEvaluator(ChatClient.builder(this.deepSeekChatModel));
    EvaluationResponse evaluationResponse = evaluator.evaluate(evaluationRequest);
    return evaluationResponse;
}
```



这里主要看下 EvaluationResponse 的几个属性

```java
// 表示是否通过评估
private final boolean pass;
// 评估打分 0～1
private final float score;
// 评估反馈
private final String feedback;
// 一些元数据信息
private final Map<String, Object> metadata;
```

评估结果主要是针对 pass 和 score，如果对于结果要求不是很高，直接关注 pass 即可；如果对结果有求较高，需要结合评估打分来控制。

