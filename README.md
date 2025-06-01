# Spring AI Summary

基于 Spring AI 框架的多模型 LLM 应用示例项目，展示如何集成和使用多种大语言模型。

## 🎯 项目简介

本项目是一个 Spring AI 实践项目，旨在通过实际案例展示 Spring AI 框架的核心功能和使用方法。项目采用模块化设计，每个模块都专注于特定的功能领域，便于学习和扩展。

### 核心特性

- **多模型支持**：集成 OpenAI、通义千问、豆包、DeepSeek 等多种 LLM 模型
- **RAG 实现**：完整的检索增强生成实现，支持文档向量化和语义搜索
- **工具调用**：支持函数调用（Function Calling）和工具集成
- **会话记忆**：支持多种存储方式的会话历史管理
- **评估系统**：AI 回答质量评估工具
- **监控统计**：Token 使用量统计和性能监控

## 🏗️ 项目结构

```
spring-ai-summary/
├── spring-ai-chat/                   # 聊天模块
│   ├── spring-ai-chat-openai/        # OpenAI 模型接入
│   ├── spring-ai-chat-qwen/          # 通义千问模型接入
│   ├── spring-ai-chat-doubao/        # 豆包模型接入
│   ├── spring-ai-chat-deepseek/      # DeepSeek 模型接入
│   ├── spring-ai-chat-multi/         # 多模型并行调用
│   └── spring-ai-chat-multi-openai/  # OpenAI 多模型并行调用
├── spring-ai-rag/                    # RAG 检索增强生成
├── spring-ai-embedding/              # 文本向量化服务
├── spring-ai-tool-calling/           # 工具函数调用示例
├── spring-ai-chat-memory/            # 会话记忆管理
├── spring-ai-evaluation/             # AI 回答评估
└── spring-ai-mcp/                    # MCP 示例
```

## 🚀 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- Docker（用于运行 Milvus）
- 网络连接（用于访问 LLM API）

### 1. 克隆项目

```bash
git clone https://github.com/glmapper/spring-ai-summary.git
cd spring-ai-summary
```

### 2. 配置环境变量

```bash
# OpenAI 配置
export OPENAI_API_KEY=your-openai-api-key

# 通义千问配置
export QWEN_API_KEY=your-qwen-api-key

# 豆包配置（可选）
export DOUBAO_API_KEY=your-doubao-api-key
```

### 3. 启动 Milvus

```bash
# 使用 docker-compose 启动 Milvus
docker-compose up -d
```

### 4. 运行示例

#### 基础聊天示例

```bash
cd spring-ai-chat/spring-ai-chat-openai
mvn spring-boot:run
```

#### RAG 示例

```bash
cd spring-ai-rag
mvn spring-boot:run
```

#### 工具调用示例

```bash
cd spring-ai-tool-calling
mvn spring-boot:run
```

## ⚙️ 配置说明

### 1. 模型配置

```yaml
spring:
  ai:
    openai:
      api-key: ${OPENAI_API_KEY}
      chat:
        options:
          model: gpt-3.5-turbo
          temperature: 0.7
          max-tokens: 2000
    qwen:
      api-key: ${QWEN_API_KEY}
      chat:
        options:
          model: qwen-turbo
          temperature: 0.7
    doubao:
      api-key: ${DOUBAO_API_KEY}
      chat:
        options:
          model: doubao-001
          temperature: 0.7
    deepseek:
      api-key: ${DEEPSEEK_API_KEY}
      chat:
        options:
          model: deepseek-chat
          temperature: 0.7
```

### 2. Milvus 配置

```yaml
spring:
  ai:
    vector:
      store:
        milvus:
          host: localhost
          port: 19530
          dimension: 2048  # 豆包向量维度
          collection-name: document_store
          index-type: IVF_FLAT
          metric-type: COSINE
```

### 3. 会话记忆配置

```yaml
spring:
  ai:
    chat:
      memory:
        type: jdbc
        jdbc:
          table-name: chat_memory
          schema: public
          initialize-schema: always
```

## 📚 模块说明

### 1. 聊天模块 (spring-ai-chat)

提供多种 LLM 模型的接入实现，支持：
- 单模型对话：支持 OpenAI、通义千问、豆包、DeepSeek 等模型
- 多模型并行调用：支持多个模型同时调用，结果对比
- 提示词模板：支持自定义提示词模板和变量替换
- Token 统计：支持输入输出 Token 统计和成本估算

### 2. RAG 模块 (spring-ai-rag)

实现检索增强生成，包含：
- 文档向量化：支持多种文档格式的向量化处理
- 向量存储：使用 Milvus 进行高效的向量存储和检索
- 语义检索：支持相似度搜索和混合检索
- 问答生成：基于检索结果生成准确的回答

### 3. 工具调用模块 (spring-ai-tool-calling)

展示如何实现工具函数调用：
- 函数定义：使用 @Tool 注解定义工具函数
- 工具注册：支持动态注册和配置工具
- 动态调用：支持运行时动态调用工具
- 结果处理：支持工具调用结果的格式化和处理

### 4. 会话记忆模块 (spring-ai-chat-memory)

提供会话历史管理：
- JDBC 持久化：支持数据库存储会话历史
- 本地文件存储：支持文件系统存储会话历史
- 会话上下文管理：支持会话上下文的管理和清理

## 🔧 开发指南

### 贡献代码

1. **Fork 项目**
   ```bash
   # 在 GitHub 上 Fork 项目
   # 克隆你的 Fork 仓库
   git clone https://github.com/your-username/spring-ai-summary.git
   cd spring-ai-summary
   ```

2. **创建特性分支**
   ```bash
   # 创建并切换到新的特性分支
   git checkout -b feature/your-feature-name
   ```

3. **开发规范**
   - 遵循项目的代码风格和命名规范
   - 确保代码通过所有测试
   - 添加必要的单元测试
   - 更新相关文档
   - 提交信息遵循 [Conventional Commits](https://www.conventionalcommits.org/) 规范

4. **提交代码**
   ```bash
   # 添加修改的文件
   git add .

   # 提交代码
   git commit -m "feat: add new feature"

   # 推送到你的 Fork 仓库
   git push origin feature/your-feature-name
   ```

5. **创建 Pull Request**
   - 在 GitHub 上创建 Pull Request
   - 填写 PR 描述，说明改动内容和原因
   - 等待代码审查和合并

### 开发环境设置

1. **IDE 配置**
   - 推荐使用 IntelliJ IDEA
   - 安装 Lombok 插件
   - 配置 Java 21 SDK

2. **Maven 配置**
   ```xml
   <properties>
       <java.version>21</java.version>
       <spring-ai.version>1.0.0</spring-ai.version>
   </properties>
   ```

3. **运行测试**
   ```bash
   # 运行所有测试
   mvn test

   # 运行特定模块的测试
   mvn test -pl spring-ai-chat
   ```

### 代码审查清单

提交 PR 前请确保：

1. **代码质量**
   - [ ] 代码符合项目规范
   - [ ] 添加了必要的注释
   - [ ] 没有重复代码
   - [ ] 变量命名清晰

2. **测试覆盖**
   - [ ] 添加了单元测试
   - [ ] 测试覆盖了主要功能
   - [ ] 所有测试通过

3. **文档更新**
   - [ ] 更新了 README（如需要）
   - [ ] 更新了 API 文档（如需要）
   - [ ] 添加了使用示例（如需要）

4. **提交规范**
   - [ ] 提交信息符合规范
   - [ ] 提交粒度合适
   - [ ] 没有敏感信息

## 📝 注意事项

1. **API 密钥安全**
   - 使用环境变量存储 API 密钥
   - 不要在代码中硬编码密钥
   - 定期轮换密钥

2. **Milvus 使用**
   - 确保创建正确的向量维度
   - 查询前需要加载集合
   - 注意索引创建

3. **Token 使用**
   - 监控 Token 消耗
   - 设置合理的限制
   - 实现缓存机制

## 📄 License

本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。

## 🙏 致谢

- [Spring AI](https://github.com/spring-projects/spring-ai) - 提供强大的 AI 集成框架
- [OpenAI](https://openai.com) - 提供 GPT 系列模型
- [通义千问](https://qianwen.aliyun.com) - 提供 Qwen 系列模型
- [豆包](https://www.volcengine.com/docs/82379) - 提供豆包系列模型
- [Milvus](https://milvus.io) - 提供向量数据库支持
