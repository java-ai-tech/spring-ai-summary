package com.glmapper.ai.workflow.core.step;


/**
 * @Classname WorkflowStep
 * @Description 工作流步骤接口
 *
 * @Date 2025/6/10 11:40
 * @Created by Gepeng18
 */
public interface WorkflowStep {
    
    /**
     * 执行步骤
     *
     * @param input 输入数据
     * @return 步骤执行结果
     */
    Object execute(Object input);
    
    /**
     * 获取步骤名称
     *
     * @return 步骤名称
     */
    String name();
} 