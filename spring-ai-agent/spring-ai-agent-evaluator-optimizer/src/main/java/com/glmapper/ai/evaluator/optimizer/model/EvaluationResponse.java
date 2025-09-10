package com.glmapper.ai.evaluator.optimizer.model;

import lombok.Data;

/**
 * Response model for the evaluation phase
 * 
 * @author glmapper
 */
@Data
public class EvaluationResponse {
    
    private double score;
    private String feedback;
    private boolean isAcceptable;
    private String improvementSuggestions;
    private int iteration;
    
    public EvaluationResponse(double score, String feedback, boolean isAcceptable, String improvementSuggestions, int iteration) {
        this.score = score;
        this.feedback = feedback;
        this.isAcceptable = isAcceptable;
        this.improvementSuggestions = improvementSuggestions;
        this.iteration = iteration;
    }
}