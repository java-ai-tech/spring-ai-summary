package com.glmapper.ai.evaluator.optimizer.controller;

import com.glmapper.ai.evaluator.optimizer.model.RefinedResponse;
import com.glmapper.ai.evaluator.optimizer.service.EvaluatorOptimizerService;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for the Evaluator-Optimizer pattern
 * 
 * @author glmapper
 */
@RestController
@RequestMapping("/api/evaluator-optimizer")
public class EvaluatorOptimizerController {

    private final EvaluatorOptimizerService evaluatorOptimizerService;

    public EvaluatorOptimizerController(EvaluatorOptimizerService evaluatorOptimizerService) {
        this.evaluatorOptimizerService = evaluatorOptimizerService;
    }

    /**
     * Execute the evaluator-optimizer loop for a given task
     * 
     * @param task The task description
     * @return RefinedResponse with the final solution and evolution trace
     */
    @PostMapping("/solve")
    public RefinedResponse solve(@RequestParam String task) {
        return evaluatorOptimizerService.loop(task);
    }

    /**
     * Execute the evaluator-optimizer loop with context and custom criteria
     * 
     * @param task The task description
     * @param context Additional context (optional)
     * @param criteria Custom evaluation criteria (optional)
     * @return RefinedResponse with the final solution and evolution trace
     */
    @PostMapping("/solve-advanced")
    public RefinedResponse solveAdvanced(@RequestParam String task,
                                       @RequestParam(required = false) String context,
                                       @RequestParam(required = false) String criteria) {
        return evaluatorOptimizerService.loop(task, context, criteria);
    }

    /**
     * Simple health check endpoint
     * 
     * @return status message
     */
    @GetMapping("/health")
    public String health() {
        return "Evaluator-Optimizer service is running";
    }
}