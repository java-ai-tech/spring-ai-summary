package com.glmapper.ai.evaluator.optimizer;

import com.glmapper.ai.evaluator.optimizer.model.RefinedResponse;
import com.glmapper.ai.evaluator.optimizer.service.EvaluatorOptimizerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for EvaluatorOptimizerService
 * 
 * @author glmapper
 */
@SpringBootTest
@ActiveProfiles("test")
class EvaluatorOptimizerServiceTest {

    @Autowired
    private EvaluatorOptimizerService evaluatorOptimizerService;

    @Test
    void testEvaluatorOptimizerLoop() {
        // Given
        String task = "Create a simple Java method that calculates the factorial of a number";
        
        // When
        RefinedResponse response = evaluatorOptimizerService.loop(task);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getFinalSolution());
        assertNotNull(response.getChainOfThought());
        assertNotNull(response.getEvaluationHistory());
        assertTrue(response.getTotalIterations() > 0);
        assertTrue(response.getTotalIterations() <= 3); // Max iterations
        assertTrue(response.getFinalScore() >= 0.0);
        assertTrue(response.getFinalScore() <= 10.0);
        
        // Chain of thought should contain iteration details
        assertTrue(response.getChainOfThought().size() > 0);
        assertTrue(response.getChainOfThought().get(0).contains("Starting Evaluator-Optimizer loop"));
        
        // Should have evaluation history
        assertEquals(response.getTotalIterations(), response.getEvaluationHistory().size());
        
        System.out.println("Final Solution: " + response.getFinalSolution());
        System.out.println("Total Iterations: " + response.getTotalIterations());
        System.out.println("Final Score: " + response.getFinalScore());
        System.out.println("Converged: " + response.isConverged());
    }

    @Test
    void testEvaluatorOptimizerLoopWithContext() {
        // Given
        String task = "Create a REST endpoint for user registration";
        String context = "This is for a Spring Boot application using Spring Security";
        
        // When
        RefinedResponse response = evaluatorOptimizerService.loop(task, context, null);
        
        // Then
        assertNotNull(response);
        assertNotNull(response.getFinalSolution());
        assertTrue(response.getTotalIterations() > 0);
        
        System.out.println("Final Solution with Context: " + response.getFinalSolution());
    }
}