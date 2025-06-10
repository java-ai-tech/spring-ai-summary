package com.glmapper.ai.workflow.model;

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
     * 响应内容
     */
    private String content;
    
    /**
     * 是否成功
     */
    private boolean success;
    
    /**
     * 错误信息
     */
    private String errorMessage;
    
}