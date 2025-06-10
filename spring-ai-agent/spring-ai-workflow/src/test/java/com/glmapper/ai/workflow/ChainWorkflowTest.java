package com.glmapper.ai.workflow;

import com.glmapper.ai.workflow.model.WorkflowRequest;
import com.glmapper.ai.workflow.model.WorkflowResponse;
import com.glmapper.ai.workflow.core.step.WorkflowStep;
import com.glmapper.ai.workflow.core.WorkflowFactory;
import com.glmapper.ai.workflow.core.WorkflowStepFactory;
import com.glmapper.ai.workflow.core.workflow.Workflow;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class ChainWorkflowTest {
    
    @Autowired
    private WorkflowStepFactory workflowStepFactory;

    @Autowired
    private WorkflowFactory workflowFactory;
    
    @Test
    @DisplayName("演示链式工作流基本用法")
    void demonstrateChainWorkflowUsage() {
        // 创建工作流步骤
        WorkflowStep outlineStep = workflowStepFactory.createAiStep(
                "大纲生成",
                "根据用户提供的主题，生成一个详细的内容大纲，包括引言、主要部分和结论。"
        );
        
        WorkflowStep expandStep = workflowStepFactory.createAiStep(
                "内容扩写", 
                "根据提供的大纲，创作一篇完整的文章。"
        );
        
        WorkflowStep polishStep = workflowStepFactory.createAiStep(
                "润色优化",
                "对提供的文章进行润色和优化，改进语言表达。"
        );
        
        // 创建链式工作流
        List<WorkflowStep> steps = Arrays.asList(outlineStep, expandStep, polishStep);
        Workflow chainWorkflow = workflowFactory.createChainWorkflow(steps);
        
        // 执行工作流
        WorkflowRequest request = WorkflowRequest.builder()
                .question("人工智能在医疗领域的应用")
                .build();
        
        WorkflowResponse response = chainWorkflow.execute(request);
        
        // 验证响应
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getContent());
    }
} 