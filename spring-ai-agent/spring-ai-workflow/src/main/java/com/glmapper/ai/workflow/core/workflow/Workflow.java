package com.glmapper.ai.workflow.core.workflow;

import com.glmapper.ai.workflow.model.WorkflowRequest;
import com.glmapper.ai.workflow.model.WorkflowResponse;


/**
 * @Classname Workflow
 * @Description 工作流核心接口
 *
 * @Date 2025/6/10 10:21
 * @Created by Gepeng18
 */
public interface Workflow {
    
    /**
     * 执行本工作流
     *
     * @param input 输入的请求
     * @return 工作流执行结果
     */
    WorkflowResponse execute(WorkflowRequest input);
} 