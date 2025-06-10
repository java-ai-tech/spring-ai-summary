package com.glmapper.ai.workflow.core.workflow.impl;

import com.glmapper.ai.workflow.model.WorkflowRequest;
import com.glmapper.ai.workflow.model.WorkflowResponse;
import com.glmapper.ai.workflow.core.workflow.Workflow;
import com.glmapper.ai.workflow.core.step.WorkflowStep;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

/**
 * @Classname RoutingWorkflow
 * @Description 路由工作流实现：基于路由规则选择合适的工作流步骤执行
 *
 * @Date 2025/6/10 16:36
 * @Created by Gepeng18
 */
@Slf4j
public class RoutingWorkflow implements Workflow {
    
    private final WorkflowStep routerSelector;
    private final Map<String, WorkflowStep> stepMap;
    
    public RoutingWorkflow(WorkflowStep routerSelector, Map<String, WorkflowStep> stepMap) {
        this.routerSelector = routerSelector;
        this.stepMap = stepMap;
    }
    
    @Override
    public WorkflowResponse execute(WorkflowRequest input) {
        try {
            log.info("开始执行路由工作流, 路由规则数量: {}", stepMap.size());
            
            // 使用路由选择器确定最合适的路由
            String routeKey = (String) routerSelector.execute(input);
            log.info("路由结果: {}", routeKey);
            
            // 查找对应的步骤
            WorkflowStep step = stepMap.get(routeKey);
            
            if (step == null) {
                log.error("未找到路由对应的步骤: {}", routeKey);
                return WorkflowResponse.builder()
                        .success(false)
                        .errorMessage("未找到路由对应的步骤: " + routeKey)
                        .build();
            }
            
            // 执行找到的步骤
            log.info("执行步骤: {}", step.name());
            Object result = step.execute(input.getQuestion());
            
            log.info("路由工作流执行完成, 执行结果为: \n {}", result);
            
            return WorkflowResponse.builder()
                    .content(result != null ? result.toString() : null)
                    .success(true)
                    .build();
            
        } catch (Exception e) {
            log.error("路由工作流执行失败", e);
            return WorkflowResponse.builder()
                    .success(false)
                    .errorMessage("工作流执行失败: " + e.getMessage())
                    .build();
        }
    }
} 