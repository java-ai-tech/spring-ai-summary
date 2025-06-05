package com.glmapper.ai.evaluation;

import com.glmapper.ai.evaluation.service.EvaluationService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.evaluation.EvaluationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname EvaluationServiceTest
 * @Description EvaluationServiceTest
 * @Date 2025/6/5 09:59
 * @Created by glmapper
 */
@SpringBootTest(classes = EvaluationApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class EvaluationServiceTest {

    @Autowired
    private EvaluationService evaluationService;

    @Test
    public void testEvaluation() {
        EvaluationResponse answer = this.evaluationService.evaluate("明朝有多少位皇帝？");
        Assertions.assertTrue(answer.isPass());
        System.out.println("AI回答: " + answer.getFeedback());
    }
}
