package com.glmapper.ai.evaluator.optimizer.service;

import com.glmapper.ai.evaluator.optimizer.model.EvaluationRequest;
import com.glmapper.ai.evaluator.optimizer.model.EvaluationResponse;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * Service responsible for evaluating generated solutions
 * 
 * @author glmapper
 */
@Service
public class EvaluatorService {

    private final ChatClient chatClient;

    private static final String EVALUATION_PROMPT = 
            "You are an expert evaluator. Please evaluate the following solution against the given task and criteria.\n" +
            "\n" +
            "Original Task: {task}\n" +
            "\n" +
            "Solution to Evaluate:\n" +
            "{solution}\n" +
            "\n" +
            "Evaluation Criteria: {criteria}\n" +
            "\n" +
            "Please provide:\n" +
            "1. A score from 0.0 to 10.0 (where 10.0 is perfect)\n" +
            "2. Detailed feedback on strengths and weaknesses\n" +
            "3. Whether this solution is acceptable (YES/NO)\n" +
            "4. Specific suggestions for improvement\n" +
            "\n" +
            "Format your response as:\n" +
            "SCORE: [0.0-10.0]\n" +
            "FEEDBACK: [detailed feedback]\n" +
            "ACCEPTABLE: [YES/NO]\n" +
            "IMPROVEMENTS: [specific suggestions]";

    private static final double ACCEPTABLE_THRESHOLD = 7.0;

    public EvaluatorService(@Qualifier("evaluatorChatClient") ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public EvaluationResponse evaluate(EvaluationRequest request) {
        String prompt = EVALUATION_PROMPT
                .replace("{task}", request.getOriginalTask())
                .replace("{solution}", request.getSolution())
                .replace("{criteria}", request.getCriteria() != null ? request.getCriteria() : getDefaultCriteria());

        String response = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        return parseEvaluationResponse(response, request.getIteration());
    }

    private EvaluationResponse parseEvaluationResponse(String response, int iteration) {
        try {
            double score = extractScore(response);
            String feedback = extractFeedback(response);
            boolean isAcceptable = extractAcceptable(response) || score >= ACCEPTABLE_THRESHOLD;
            String improvements = extractImprovements(response);

            return new EvaluationResponse(score, feedback, isAcceptable, improvements, iteration);
        } catch (Exception e) {
            // Fallback in case of parsing errors
            return new EvaluationResponse(5.0, response, false, "Unable to parse specific improvements", iteration);
        }
    }

    private double extractScore(String response) {
        try {
            String scoreLine = findLine(response, "SCORE:");
            String scoreStr = scoreLine.substring(scoreLine.indexOf(":") + 1).trim();
            return Double.parseDouble(scoreStr);
        } catch (Exception e) {
            return 5.0; // Default score
        }
    }

    private String extractFeedback(String response) {
        try {
            return findLine(response, "FEEDBACK:").substring("FEEDBACK:".length()).trim();
        } catch (Exception e) {
            return "No detailed feedback available";
        }
    }

    private boolean extractAcceptable(String response) {
        try {
            String acceptableLine = findLine(response, "ACCEPTABLE:");
            return acceptableLine.toUpperCase().contains("YES");
        } catch (Exception e) {
            return false;
        }
    }

    private String extractImprovements(String response) {
        try {
            return findLine(response, "IMPROVEMENTS:").substring("IMPROVEMENTS:".length()).trim();
        } catch (Exception e) {
            return "No specific improvements suggested";
        }
    }

    private String findLine(String response, String prefix) {
        String[] lines = response.split("\n");
        for (String line : lines) {
            if (line.trim().toUpperCase().startsWith(prefix.toUpperCase())) {
                return line;
            }
        }
        throw new RuntimeException("Line with prefix '" + prefix + "' not found");
    }

    private String getDefaultCriteria() {
        return "- Correctness: Does the solution solve the problem correctly?\n" +
               "- Completeness: Are all requirements addressed?\n" +
               "- Clarity: Is the solution easy to understand?\n" +
               "- Best Practices: Does it follow good coding/design practices?\n" +
               "- Efficiency: Is the solution reasonably efficient?";
    }
}