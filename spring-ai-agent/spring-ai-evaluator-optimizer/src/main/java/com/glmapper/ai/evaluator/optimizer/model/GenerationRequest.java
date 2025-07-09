package com.glmapper.ai.evaluator.optimizer.model;

import lombok.Data;

/**
 * Request model for the generation phase
 * 
 * @author glmapper
 */
@Data
public class GenerationRequest {
    
    private String task;
    private String context;
    private String previousAttempt;
    private String feedback;
    
    public GenerationRequest(String task) {
        this.task = task;
    }
    
    public GenerationRequest(String task, String context) {
        this.task = task;
        this.context = context;
    }
    
    public GenerationRequest(String task, String context, String previousAttempt, String feedback) {
        this.task = task;
        this.context = context;
        this.previousAttempt = previousAttempt;
        this.feedback = feedback;
    }
}