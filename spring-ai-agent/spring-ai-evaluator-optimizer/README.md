# Spring AI 评估器-优化器 智能体

这个模块实现了**评估器-优化器模式**，通过生成、评估和优化循环对AI生成的解决方案进行迭代改进。

## 快速开始 🚀

### 1. 启动应用
```bash
cd spring-ai-evaluator-optimizer
mvn spring-boot:run
```

应用将在端口 8084 启动。

### 2. 测试健康检查
```bash
curl http://localhost:8084/api/evaluator-optimizer/health
```

### 3. 基本任务求解示例
```bash
curl -X POST "http://localhost:8084/api/evaluator-optimizer/solve?task=创建一个计算斐波那契数列的Java方法"
```

### 4. 高级任务求解示例
```bash
curl -X POST "http://localhost:8084/api/evaluator-optimizer/solve-advanced" \
  -d "task=创建一个用户管理的REST API端点" \
  -d "context=Spring Boot应用，使用Spring Security和JPA" \
  -d "criteria=必须包含输入验证、错误处理和合适的HTTP状态码" \
  -H "Content-Type: application/x-www-form-urlencoded"
```

## 概述

评估器-优化器模式适用于以下场景：
- 有明确评估标准的任务
- 迭代改进能提供可衡量价值的场景
- 需要多轮评估和改进的复杂任务

## 架构设计

### 核心组件

1. **GeneratorService（生成器服务）**: 生成初始解决方案并基于反馈进行改进
2. **EvaluatorService（评估器服务）**: 对生成的解决方案进行批判性评估和评分
3. **EvaluatorOptimizerService（评估优化服务）**: 编排迭代改进循环
4. **模型类**: 为每个阶段提供类型安全的请求/响应对象

### 工作流程

1. **生成阶段**: 为给定任务创建初始解决方案
2. **评估阶段**: 对生成的解决方案进行评分和批评
3. **改进循环**: 基于反馈迭代改进解决方案
4. **收敛判断**: 当达到可接受质量或超过最大迭代次数时停止

## 配置说明

### 环境变量

应用支持多种AI模型配置，当前使用阿里云兼容模式：

```yaml
spring:
  ai:
    openai:
      api-key: 你的API密钥
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen-plus
          temperature: 0.7
    deepseek:
      api-key: 你的API密钥  
      base-url: https://dashscope.aliyuncs.com/compatible-mode
      chat:
        options:
          model: qwen-max
          temperature: 0.3
```

### 模型配置

- **生成器**: Qwen-Plus (temperature: 0.7) 用于创造性的解决方案生成
- **评估器**: Qwen-Max (temperature: 0.3) 用于一致性评估

## 使用方法

### API 接口

#### 基本任务求解
```bash
curl -X POST "http://localhost:8084/api/evaluator-optimizer/solve?task=创建一个计算斐波那契数列的Java方法"
```

#### 带上下文的高级任务求解
```bash
curl -X POST "http://localhost:8084/api/evaluator-optimizer/solve-advanced" \
  -d "task=创建一个用户管理的REST端点" \
  -d "context=Spring Boot应用，使用JPA和安全框架" \
  -d "criteria=必须包含适当的验证和错误处理" \
  -H "Content-Type: application/x-www-form-urlencoded"
```

#### 健康检查
```bash
curl http://localhost:8084/api/evaluator-optimizer/health
```

### 响应示例

```json
{
  "finalSolution": "public class FibonacciCalculator {...}",
  "chainOfThought": [
    "开始为任务执行评估器-优化器循环: 创建斐波那契计算器",
    "第1次迭代 - 生成解决方案: public class FibonacciCalculator...",
    "第1次迭代 - 评分: 9.5, 可接受: true",
    "在第1次迭代收敛，评分9.5"
  ],
  "evaluationHistory": [...],
  "totalIterations": 1,
  "finalScore": 9.5,
  "converged": true
}
```

## 配置参数

- **最大迭代次数**: 3次（最大改进循环次数）
- **收敛阈值**: 8.5分（可接受解决方案的评分阈值）
- **接受阈值**: 7.0分（接受的最低评分）

## 测试

运行测试套件：

```bash
mvn test
```

测试包括：
- 基本评估器-优化器循环功能
- 上下文感知的任务求解
- 与实际AI模型的集成测试

## 使用场景

### 理想应用
- 代码生成和优化
- 技术文档创建
- 多种解决方案的问题求解
- 迭代改进的创意写作

### 示例任务
- "创建一个线程安全的Java缓存实现"
- "设计一个图书管理系统的REST API"
- "为排序算法编写全面的单元测试"
- "创建电商平台的数据库模式"

## 实现说明

### 生成策略
- 初始生成专注于完整性和功能性
- 改进生成针对特定反馈要点
- 保持成功要素的同时改进问题区域

### 评估标准
- **正确性**: 解决方案是否正确解决了问题？
- **完整性**: 是否满足了所有需求？
- **清晰度**: 解决方案是否易于理解？
- **最佳实践**: 是否遵循良好的编码/设计实践？
- **效率**: 解决方案是否合理高效？

### 收敛逻辑
- 当解决方案评分 ≥ 8.5 且标记为可接受时停止
- 最多3次迭代以防止无限循环
- 提供演化轨迹以保证透明度

## 依赖项

- Spring Boot 3.3.6
- Spring AI 1.0.0
- Spring AI OpenAI Starter
- Spring AI DeepSeek Starter
- Lombok 用于减少样板代码

## 测试结果 ✅

基于实际功能测试，该模块表现优异：

- **✅ 应用启动**: Java 21环境下成功启动
- **✅ 健康检查**: 所有端点正常响应
- **✅ 基本求解**: 斐波那契计算示例，评分9.5/10，1次迭代收敛
- **✅ 高级求解**: 支持上下文和自定义标准，评分8.5/10
- **✅ 评估循环**: Generator和Evaluator协同工作正常
- **✅ 配置管理**: 阿里云兼容模式配置成功

## 未来增强

- 可配置的评估标准
- 自定义评分模型
- 多评估器共识机制
- 解决方案比较和排名
- 性能指标和分析