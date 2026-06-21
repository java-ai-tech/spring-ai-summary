# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Spring AI Summary 是基于 Spring AI 框架的模块化示例工程集合，用于学习 Spring AI 核心功能。包含聊天、向量数据库、RAG、MCP、Agent 等多个功能模块的示例代码。

## 技术栈

- **Java**: 21
- **Spring Boot**: 3.3.6
- **Spring AI**: 1.0.0
- **构建工具**: Maven 3.6+ (推荐使用 mvnd 加速)
- **Lombok**: 用于简化 POJO 代码

## 常用命令

```bash
# 编译整个项目
mvn clean compile -DskipTests

# 编译单个模块 (如 spring-ai-chat-deepseek)
mvn clean compile -DskipTests -pl spring-ai-chat/spring-ai-chat-deepseek

# 运行测试
mvn test -pl <module-path>

# 打包
mvn clean package -DskipTests
```

## 模块结构

```
spring-ai-summary/
├── spring-ai-chat/          # 聊天示例 (DeepSeek, Doubao, OpenAI, Qwen, Ollama, 多模型)
├── spring-ai-vector/        # 向量数据库 (MariaDB, Redis, Milvus)
├── spring-ai-rag/           # RAG 检索增强生成
├── spring-ai-mcp/           # MCP 协议 (客户端/服务端 + Nacos 集成)
├── spring-ai-evaluation/    # 模型评估
├── spring-ai-chat-memory/   # 对话记忆 (JDBC, 本地存储)
├── spring-ai-tool-calling/  # 工具调用
├── spring-ai-agent/         # Agent 模式 (工作流、评估优化、编排)
└── spring-ai-observability/ # 可观测性 (指标、链路追踪)
```

## 配置说明

每个子模块在 `src/main/resources/` 下有独立的配置文件 (application.properties 或 application.yml)。API 密钥通过环境变量注入：

```properties
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.openai.api-key=${OPENAI_API_KEY}
```

敏感配置文件 (如 `application-deepseek.properties`) 已被 `.gitignore` 排除。

## 运行示例

1. 配置对应模块的 API 密钥环境变量
2. 运行模块的 Application 主类 (如 `DsChatApplication`)
3. 默认端口在各模块配置文件中定义 (通常 8080-8082)
4. 通过 Actuator 端点监控: `http://localhost:<port>/actuator`

## 学习路径建议

1. `spring-ai-chat` - 聊天应用开发 (必选起点)
2. `spring-ai-tool-calling` - 工具调用能力
3. `spring-ai-vector` - 向量数据库集成
4. `spring-ai-mcp` / `spring-ai-rag` / `spring-ai-agent` - 高级应用模式