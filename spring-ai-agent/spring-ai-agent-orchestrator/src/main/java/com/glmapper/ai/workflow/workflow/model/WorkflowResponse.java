package com.glmapper.ai.workflow.workflow.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname WorkflowResponse
 * @Description 工作流响应
 *
 * @Date 2025/6/10 14:32
 * @Created by Gepeng18
 */
@Data
@Builder
public class WorkflowResponse {
    
    /**
     * 大模型对任务的分析/拆解原始输出
     */
    private String analysis;
    
    /**
     * 拆解出的所有子任务
     */
    private java.util.List<String> subtasks;
    
    /**
     * 每个 worker 的输出结果
     */
    private java.util.List<String> workerResponses;
    
    /**
     * 最终合成的内容（如汇总、总结、最终答案等）
     */
    private String content;
    
    /**
     * 总体是否成功
     */
    private boolean success;
    
    /**
     * 错误信息（如有）
     */
    private String errorMessage;
    
}