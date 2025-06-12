package com.glmapper.ai.workflow.workflow.model;

import lombok.Builder;
import lombok.Data;

/**
 * @Classname WorkflowRequest
 * @Description 用户请求
 *
 * @Date 2025/6/10 11:23
 * @Created by Gepeng18
 */
@Data
@Builder
public class WorkflowRequest {
    
    /**
     * 请求内容
     */
    private String question;

} 