# Spring AI Summary

基于 Spring AI 框架的多模型 LLM 应用示例项目，支持多种大语言模型的集成和使用。

## 项目介绍

本项目展示了如何使用 Spring AI 框架集成多种大语言模型，包括：

- OpenAI
- DeepSeek
- 豆包（火山引擎）

同时实现了以下功能：

- 多模型并行调用
- Token 使用量统计
- 自定义提示词模板
- 嵌入向量服务

## 技术栈

- Java 21
- Spring Boot 3.3.6
- Spring AI 1.0.0
- Micrometer (用于指标统计)

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+

### 配置

1. 克隆项目

```bash
git clone https://github.com/glmapper/spring-ai-summary.git
cd spring-ai-summary
```

2. 配置环境变量

```bash
# OpenAI
export OPENAI_API_KEY=your-openai-api-key

# DeepSeek
export DEEPSEEK_API_KEY=your-deepseek-api-key

# 豆包（火山引擎）
export DOUBAO_API_KEY=your-doubao-api-key
```

3. 修改配置文件
   在 `src/main/resources` 目录下配置相应的 properties 文件：

- `application-openai.properties`
- `application-deepseek.properties`
- `application-doubao.properties`

### 运行

```bash
mvn spring-boot:run
```

## 功能特性

### 1. 多模型并行调用

通过 `MultiModelService` 实现多个模型的并行调用，支持：

- 自定义模型参数
- 独立配置每个模型
- 并行处理响应

### 2. Token 使用量统计

使用 `SimpleMetricAdvisor` 实现 Token 使用量的统计：

- 输入 Token 统计
- 输出 Token 统计
- 总 Token 统计

可通过 Actuator 端点查看统计信息：

- `/actuator/metrics/ai.prompt.tokens`
- `/actuator/metrics/ai.completion.tokens`
- `/actuator/metrics/ai.total.tokens`

### 3. 自定义提示词模板

支持自定义提示词模板，包括：

- 默认模板渲染
- 自定义分隔符
- 参数化提示词

### 4. 嵌入向量服务

提供文本嵌入向量服务，支持：

- 文本向量化
- 向量相似度计算

## API 接口

### 聊天接口

- `GET /api/chat/prompt`: 基础聊天接口
- `GET /api/chat/chatPrompt`: 带日志的聊天接口
- `GET /api/chat/chatPromptTemplates`: 使用模板的聊天接口

### 多模型接口

- `GET /api/multi-chat/multiClientFlow`: 多模型并行调用接口

### 嵌入向量接口

- `GET /api/embedding`: 文本向量化接口

## 监控指标

项目集成了 Spring Boot Actuator，提供以下监控指标：

- Token 使用量统计
- 应用健康状态
- 性能指标

访问 `http://localhost:8081/actuator` 查看所有可用指标。

## 注意事项

1. 确保正确配置所有必要的 API 密钥
2. 注意 API 调用限制和成本控制
3. 建议在生产环境中配置适当的监控和告警机制

## 贡献指南

欢迎提交 Issue 和 Pull Request 来帮助改进项目。

## 许可证和说明

本项目为个人学习和研究使用，遵循 MIT 许可证。请在使用时注明出处。
