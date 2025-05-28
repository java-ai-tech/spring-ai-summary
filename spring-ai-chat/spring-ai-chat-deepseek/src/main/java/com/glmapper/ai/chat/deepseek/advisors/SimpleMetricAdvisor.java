package com.glmapper.ai.chat.deepseek.advisors;

import io.micrometer.core.instrument.Metrics;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;

/**
 * @Classname SimpleMetricAdvisor
 * @Description 一个简单的 Metrics Advisor 实现，用于计算输入和输出消息的 token 数量
 * <p>
 * Spring AI 在调用 LLM 后返回的是 ChatResponse 对象，该对象中包含了生成响应时的元信息（metadata），包括 token 使用情况。
 * Spring AI 本身并不在客户端对 token 做估算，而是依赖底层模型返回的 usage 信息来确定 token 数量。这也是 OpenAI 和其他厂商计算费用的依据。
 * </p>
 * @Date 2025/5/27 11:11
 * @Created by glmapper
 */
public class SimpleMetricAdvisor implements CallAdvisor {

    @Override
    public ChatClientResponse adviseCall(ChatClientRequest request, CallAdvisorChain chain) {
        // 执行实际的调用
        ChatClientResponse response = chain.nextCall(request);
        // 获取响应的使用情况
        Usage usage = response.chatResponse().getMetadata().getUsage();
        // 输入 Prompt 所使用的 token 数
        int promptTokens = usage.getPromptTokens();
        // 输出 Completion 所使用的 token 数
        int completionTokens = usage.getCompletionTokens();
        // totalTokens：总 token 数（前两者之和）
        int totalTokens = usage.getTotalTokens();
        Metrics.counter("ai.prompt.tokens").increment(promptTokens);
        Metrics.counter("ai.completion.tokens").increment(completionTokens);
        Metrics.counter("ai.total.tokens").increment(totalTokens);
        return response;
    }

    @Override
    public String getName() {
        return "simple-metric-advisor";
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
