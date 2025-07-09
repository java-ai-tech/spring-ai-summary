package com.glmapper.ai.evaluator.optimizer.model;

import lombok.Data;

/**
 * Response model for the generation phase
 * 
 * @author glmapper
 */
@Data
public class GenerationResponse {
    
    private String solution;
    private String reasoning;
    private int iteration;
    
    public GenerationResponse(String solution, String reasoning, int iteration) {
        this.solution = solution;
        this.reasoning = reasoning;
        this.iteration = iteration;
    }
}