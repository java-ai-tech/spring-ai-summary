package com.glmapper.ai.evaluator.optimizer.model;

import lombok.Data;

/**
 * Request model for the evaluation phase
 * 
 * @author glmapper
 */
@Data
public class EvaluationRequest {
    
    private String originalTask;
    private String solution;
    private String criteria;
    private int iteration;
    
    public EvaluationRequest(String originalTask, String solution, String criteria, int iteration) {
        this.originalTask = originalTask;
        this.solution = solution;
        this.criteria = criteria;
        this.iteration = iteration;
    }
}