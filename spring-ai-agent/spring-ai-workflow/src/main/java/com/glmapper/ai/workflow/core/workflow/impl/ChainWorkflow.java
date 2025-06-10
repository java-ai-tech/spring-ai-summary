package com.glmapper.ai.workflow.core.workflow.impl;

import com.glmapper.ai.workflow.model.WorkflowRequest;
import com.glmapper.ai.workflow.model.WorkflowResponse;
import com.glmapper.ai.workflow.core.workflow.Workflow;
import com.glmapper.ai.workflow.core.step.WorkflowStep;
import lombok.extern.slf4j.Slf4j;

import java.util.List;


/**
 * @Classname ChainWorkflow
 * @Description 链式工作流实现：按顺序执行一系列工作流步骤，前一步骤的输出作为后一步骤的输入
 *
 * @Date 2025/6/10 14:21
 * @Created by Gepeng18
 */
@Slf4j
public class ChainWorkflow implements Workflow {
    
    private final List<WorkflowStep> steps;
    
    public ChainWorkflow(List<WorkflowStep> steps) {
        this.steps = steps;
    }
    
    @Override
    public WorkflowResponse execute(WorkflowRequest input) {
        Object currentInput = input.getQuestion();
        
        try {
            log.info("开始执行链式工作流, 步骤数量: {}", steps.size());
            
            for (WorkflowStep step : steps) {
                log.info("执行步骤: {}, 模型输入：{}", step.name(), currentInput);
                currentInput = step.execute(currentInput);
            }
            
            log.info("链式工作流执行完成");
            return WorkflowResponse.builder()
                    .content(currentInput != null ? currentInput.toString() : null)
                    .success(true)
                    .build();
            
        } catch (Exception e) {
            log.error("链式工作流执行失败", e);
            return WorkflowResponse.builder()
                    .success(false)
                    .errorMessage("工作流执行失败: " + e.getMessage())
                    .build();
        }
    }
} 