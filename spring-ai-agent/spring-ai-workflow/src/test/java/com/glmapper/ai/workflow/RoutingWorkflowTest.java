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

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
public class RoutingWorkflowTest {
    
    @Autowired
    private WorkflowStepFactory workflowStepFactory;

    @Autowired
    private WorkflowFactory workflowFactory;
    
    @Test
    @DisplayName("演示传统路由工作流基本用法 - 使用函数进行路由")
    void demonstrateLegacyRoutingWorkflowUsage() {

        // 创建各种专门的处理步骤，这里主要采用mock数据
        Map<String, WorkflowStep> stepMap = new HashMap<>();
        
        // 账单查询处理步骤
        stepMap.put("billing", workflowStepFactory.createAiStep(
                "账单查询处理",
                "如果用户问你关于账单的问题，你就回答：存在两个账单。其他内容不需要回答。"
        ));
        
        // 技术支持步骤
        stepMap.put("technical", workflowStepFactory.createAiStep(
                "技术支持处理", 
                "如果用户问你有关技术支持的问题，你就回答：电脑该重启了。其他内容不需要回答。"
        ));
        
        // 产品信息步骤
        stepMap.put("product", workflowStepFactory.createAiStep(
                "产品信息处理",
                "如果用户问你关于产品信息的问题，你就回答：这个产品还不错。其他内容不需要回答。"
        ));
        
        // 订单状态步骤
        stepMap.put("order", workflowStepFactory.createAiStep(
                "订单状态处理",
                "如果用户问你关于订单状态的问题，你就回答：没有订单。其他内容不需要回答。"
        ));
        
        // 通用查询步骤
        stepMap.put("general", workflowStepFactory.createAiStep(
                "通用查询处理",
                "如果用户问你通用的问题，你就回答：通用问题请查询搜索引擎。其他内容不需要回答。"
        ));
        
        // 创建路由工作流
        Workflow routingWorkflow = workflowFactory.createRoutingWorkflow(stepMap);
        
        // 执行工作流
        WorkflowRequest request = WorkflowRequest.builder()
                .question("我想查看我的账单详情")
                .build();
        
        WorkflowResponse response = routingWorkflow.execute(request);

        // 验证响应
        assertNotNull(response);
        assertTrue(response.isSuccess());
        assertNotNull(response.getContent());
    }
} 