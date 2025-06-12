package com.glmapper.ai.workflow;

import com.glmapper.ai.workflow.workflow.OrchestratorWorkersWorkflow;
import com.glmapper.ai.workflow.workflow.model.WorkflowResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Unit test for simple App.
 */
@SpringBootTest(classes = OrchestratorWorkersWorkflowApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class OrchestratorWorkersWorkflowTest {

    @Autowired
    private OrchestratorWorkersWorkflow orchestratorWorkersWorkflow;

    @Test
    public void test() {
        WorkflowResponse response = orchestratorWorkersWorkflow.process("Generate both technical and user-friendly documentation for a REST API endpoint");
        // 执行结果见 resources/test_result.md
        System.out.println("Analysis: " + response.getAnalysis());
        System.out.println("Worker Outputs: " + response.getWorkerResponses());
    }
}
