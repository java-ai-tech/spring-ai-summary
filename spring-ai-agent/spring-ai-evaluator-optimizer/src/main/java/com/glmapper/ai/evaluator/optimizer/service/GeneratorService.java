package com.glmapper.ai.evaluator.optimizer.service;

import com.glmapper.ai.evaluator.optimizer.model.GenerationRequest;
import com.glmapper.ai.evaluator.optimizer.model.GenerationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service responsible for generating solutions
 * 
 * @author glmapper
 */
@Service
public class GeneratorService {

    private final ChatClient chatClient;

    private static final String GENERATION_PROMPT = 
            "Task: {task}\n" +
            "\n" +
            "{context}\n" +
            "\n" +
            "{refinement_instructions}\n" +
            "\n" +
            "Please provide a solution that is:\n" +
            "1. Complete and functional\n" +
            "2. Well-structured and clear\n" +
            "3. Addresses all requirements\n" +
            "4. Includes explanatory comments where needed\n" +
            "\n" +
            "Provide your reasoning for the approach you chose.";

    private static final String REFINEMENT_PROMPT = 
            "Task: {task}\n" +
            "\n" +
            "Previous attempt:\n" +
            "{previous_attempt}\n" +
            "\n" +
            "Feedback received:\n" +
            "{feedback}\n" +
            "\n" +
            "Please improve the solution based on the feedback. Focus on:\n" +
            "1. Addressing the specific issues mentioned in the feedback\n" +
            "2. Maintaining what worked well in the previous attempt\n" +
            "3. Ensuring the solution is better than before\n" +
            "\n" +
            "Provide your reasoning for the improvements made.";

    public GeneratorService(@Qualifier("generatorChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public GenerationResponse generate(GenerationRequest request) {
        String prompt = buildPrompt(request);
        
        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        // Extract solution and reasoning from the response
        String[] parts = response.split("Reasoning:");
        String solution = parts[0].trim();
        String reasoning = parts.length > 1 ? parts[1].trim() : "No reasoning provided";

        return new GenerationResponse(solution, reasoning, request.getPreviousAttempt() == null ? 1 : 2);
    }

    private String buildPrompt(GenerationRequest request) {
        if (request.getPreviousAttempt() == null) {
            // Initial generation
            return GENERATION_PROMPT
                    .replace("{task}", request.getTask())
                    .replace("{context}", request.getContext() != null ? "Context: " + request.getContext() : "")
                    .replace("{refinement_instructions}", "");
        } else {
            // Refinement generation
            return REFINEMENT_PROMPT
                    .replace("{task}", request.getTask())
                    .replace("{previous_attempt}", request.getPreviousAttempt())
                    .replace("{feedback}", request.getFeedback() != null ? request.getFeedback() : "No specific feedback provided");
        }
    }
}