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
public class ParallelizationWorkflowTest {
    
    @Autowired
    private WorkflowStepFactory workflowStepFactory;

    @Autowired
    private WorkflowFactory workflowFactory;
    
    @Test
    @DisplayName("演示并行工作流基本用法")
    void demonstrateParallelWorkflowUsage() {
        // 创建工作流步骤
        WorkflowStep marketingStep = workflowStepFactory.createAiStep(
                "市场营销评估",
                "从市场营销角度对产品进行评估，包括市场定位和目标受众分析"
        );
        
        WorkflowStep productManagerStep = workflowStepFactory.createAiStep(
                "产品经理评估", 
                "从产品管理角度对产品进行评估，分析产品定位和功能"
        );
        
        WorkflowStep uxDesignerStep = workflowStepFactory.createAiStep(
                "用户体验评估",
                "从用户体验角度对产品进行评估，分析界面设计和交互流程"
        );
        
        // 创建并行工作流
        List<WorkflowStep> steps = Arrays.asList(
                marketingStep, productManagerStep, uxDesignerStep
        );
        Workflow parallelWorkflow = workflowFactory.createParallelizationWorkflow(steps);
        
        // 执行工作流
        WorkflowRequest request = WorkflowRequest.builder()
                .question("智能家居控制系统，通过手机App控制家中设备")
                .build();
        
        WorkflowResponse response = parallelWorkflow.execute(request);
        
        // 验证响应
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getContent());
    }
} 