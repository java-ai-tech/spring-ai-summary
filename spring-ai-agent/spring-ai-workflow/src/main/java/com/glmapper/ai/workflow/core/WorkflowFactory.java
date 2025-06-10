package com.glmapper.ai.workflow.core;

import com.glmapper.ai.workflow.core.workflow.Workflow;
import com.glmapper.ai.workflow.core.step.WorkflowStep;
import com.glmapper.ai.workflow.core.workflow.impl.ChainWorkflow;
import com.glmapper.ai.workflow.core.workflow.impl.ParallelizationWorkflow;
import com.glmapper.ai.workflow.core.workflow.impl.RoutingWorkflow;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Classname WorkflowFactory
 * @Description 工作流工厂
 *
 * @Date 2025/6/10 15:40
 * @Created by Gepeng18
 */
@Component
@AllArgsConstructor
public class WorkflowFactory {

    private final WorkflowStepFactory workflowStepFactory;

    /**
     * 链式工作流实现：按顺序执行一系列工作流步骤，前一步骤的输出作为后一步骤的输入
     *
     * @param steps 工作流步骤
     * @return 创建的工作流
     */
    public Workflow createChainWorkflow(List<WorkflowStep> steps) {
        return new ChainWorkflow(steps);
    }

    /**
     * 并行工作流实现：同时执行多个工作流步骤，所有步骤使用相同的输入，最终结果是所有步骤结果的集合
     *
     * @param steps 工作流步骤
     * @return 创建的工作流
     */
    public Workflow createParallelizationWorkflow(List<WorkflowStep> steps) {
        return new ParallelizationWorkflow(steps);
    }

    /**
     * 路由工作流实现：基于路由规则选择合适的工作流步骤执行
     *
     * @param stepMap 工作流步骤
     * @return 创建的工作流
     */
    public Workflow createRoutingWorkflow(Map<String, WorkflowStep> stepMap) {
        // 创建AI路由选择器
        WorkflowStep routerSelector = workflowStepFactory.createAiRouterSelector("AI路由选择器", stepMap);
        return new RoutingWorkflow(routerSelector, stepMap);
    }
}