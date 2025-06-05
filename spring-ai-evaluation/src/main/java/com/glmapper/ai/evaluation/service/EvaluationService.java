package com.glmapper.ai.evaluation.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.evaluation.RelevancyEvaluator;
import org.springframework.ai.deepseek.DeepSeekChatModel;
import org.springframework.ai.evaluation.EvaluationRequest;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;

/**
 * @Classname EvaluationService
 * @Description 用 deepseek 评估 豆包模型的能力
 * @Date 2025/6/5 09:22
 * @Created by glmapper
 */
@Component
public class EvaluationService {

    private static String prompt = "Your task is to evaluate if the response for the query\n" + "is in line with the context information provided.\n" + "\n" + "You have two options to answer. Either YES or NO.\n" + "\n" + "Answer YES, if the response for the query\n" + "is in line with context information otherwise NO.\n" + "\n" + "Query:\n" + "{query}\n" + "\n" + "Response:\n" + "{response}\n" + "\n" + "Context:\n" + "{context}\n" + "\n" + "Answer:";

    @Autowired
    private ChatClient openAiChatClient;

    @Autowired
    private ChatClient deepSeekChatClient;

    @Autowired
    private DeepSeekChatModel deepSeekChatModel;

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
}
