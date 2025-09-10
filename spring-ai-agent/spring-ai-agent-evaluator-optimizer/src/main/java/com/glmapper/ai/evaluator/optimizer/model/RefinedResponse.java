package com.glmapper.ai.evaluator.optimizer.model;

import lombok.Data;

import java.util.List;

/**
 * Final response model containing the refined solution and evolution trace
 * 
 * @author glmapper
 */
@Data
public class RefinedResponse {
    
    private String finalSolution;
    private List<String> chainOfThought;
    private List<EvaluationResponse> evaluationHistory;
    private int totalIterations;
    private double finalScore;
    private boolean converged;
    
    public RefinedResponse(String finalSolution, List<String> chainOfThought, 
                          List<EvaluationResponse> evaluationHistory, 
                          int totalIterations, double finalScore, boolean converged) {
        this.finalSolution = finalSolution;
        this.chainOfThought = chainOfThought;
        this.evaluationHistory = evaluationHistory;
        this.totalIterations = totalIterations;
        this.finalScore = finalScore;
        this.converged = converged;
    }
}