package com.glmapper.ai.workflow.core.workflow.impl;

import com.glmapper.ai.workflow.model.WorkflowRequest;
import com.glmapper.ai.workflow.model.WorkflowResponse;
import com.glmapper.ai.workflow.core.workflow.Workflow;
import com.glmapper.ai.workflow.core.step.WorkflowStep;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;


/**
 * @Classname ParallelizationWorkflow
 * @Description 并行工作流实现：同时执行多个工作流步骤，所有步骤使用相同的输入，最终结果是所有步骤结果的集合
 *
 * @Date 2025/6/10 14:27
 * @Created by Gepeng18
 */
@Slf4j
public class ParallelizationWorkflow implements Workflow {
    
    private final List<WorkflowStep> steps;
    
    public ParallelizationWorkflow(List<WorkflowStep> steps) {
        this.steps = steps;
    }
    
    @Override
    public WorkflowResponse execute(WorkflowRequest input) {
        try {
            log.info("开始执行并行工作流, 步骤数量: {}", steps.size());
            
            List<CompletableFuture<Map.Entry<String, Object>>> futures = new ArrayList<>();
            
            // 为每个步骤创建一个异步任务
            for (WorkflowStep step : steps) {
                CompletableFuture<Map.Entry<String, Object>> future = CompletableFuture.supplyAsync(() -> {
                    log.info("执行步骤: {}", step.name());
                    Object result = step.execute(input.getQuestion());
                    return Map.entry(step.name(), result);
                });
                futures.add(future);
            }
            
            // 等待所有异步任务完成
            CompletableFuture<Void> allFutures = CompletableFuture.allOf(
                    futures.toArray(new CompletableFuture[0])
            );
            
            // 收集所有结果
            CompletableFuture<Map<String, Object>> resultFuture = allFutures.thenApply(v ->
                    futures.stream()
                            .map(CompletableFuture::join)
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))
            );
            
            Map<String, Object> results = resultFuture.get();
            String content = formatResults(results);
            log.info("并行工作流执行完成，结果数量: {}, 执行结果为：\n {}", results.size(), content);

            return WorkflowResponse.builder()
                    .success(true)
                    .content(content)
                    .build();
            
        } catch (InterruptedException | ExecutionException e) {
            log.error("并行工作流执行失败", e);
            return WorkflowResponse.builder()
                    .success(false)
                    .errorMessage("工作流执行失败: " + e.getMessage())
                    .build();
        }
    }
    
    /**
     * 格式化结果为可读字符串
     *
     * @param results 结果映射
     * @return 格式化的结果字符串
     */
    private String formatResults(Map<String, Object> results) {
        StringBuilder sb = new StringBuilder();
        sb.append("并行工作流执行结果:\n");
        
        results.forEach((key, value) -> {
            sb.append("步骤 [").append(key).append("]: \n");
            sb.append(value != null ? value.toString() : "null").append("\n\n");
        });
        
        return sb.toString();
    }
} 