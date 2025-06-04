# Spring AI Summary

<p align="left">
  <a href="README.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;margin-right:10px;">🇨🇳 中文</span></a>
  <a href="README_EN.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;">🇺🇸 English</span></a>
</p>

![Spring AI Summary](https://img.shields.io/badge/spring--ai--summary-v1.0.0-blue.svg)

🚀🚀🚀 本项目是一个 Spring AI 快速入门的样例工程项目，旨在通过一些小的案例展示 Spring AI 框架的核心功能和使用方法。
项目采用模块化设计，每个模块都专注于特定的功能领域，便于学习和扩展。

## 📖 关于 Spring AI

Spring AI 项目的目标是简化集成人工智能功能的应用程序的开发过程，避免引入不必要的复杂性。关于 Spring AI 的更多信息，请访问 [Spring AI 官方文档](https://spring.io/projects/spring-ai)。

## 🗂️ 项目结构

本工程采用模块化设计，按照功能特性主要划分为以下几个模块：

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

## 🧩 核心功能实现

本案例工程的核心功能实现包括：

- **多模型支持**：集成 OpenAI、通义千问、豆包、DeepSeek 等多种 LLM 模型
- **RAG 实现**：完整的检索增强生成实现，支持文档向量化和语义搜索
- **Function Calling**：支持函数调用（Function Calling）和工具集成
- **Chat Memory**：支持多种存储方式的会话历史管理
- **评估系统**：AI 回答质量评估工具
- **监控统计**：Token 使用量统计和性能监控

下面你可以通过快速开始部分来快速运行项目。


## 🚀 快速开始

### ⚙️ 环境要求

- SpringBoot 3.3.6
- Spring AI 1.0.0
- JDK 21+
- Maven 3.6+
- Docker（用于运行 Milvus）

### 1. 🧬 克隆项目

```bash
git clone https://github.com/glmapper/spring-ai-summary.git
cd spring-ai-summary
```

### 2. 🛠️ 配置环境变量

对于每个模块的 resource 文件夹下的 `application.yml`/`application.properties` 文件，根据你的需求配置相应的 API 密钥。如 **spring-ai-chat-deepseek** 模块：
```properties
# because we do not use the OpenAI protocol
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.deepseek.base-url=https://api.deepseek.com
spring.ai.deepseek.chat.completions-path=/v1/chat/completions
spring.ai.deepseek.chat.options.model=deepseek-chat
```
将你的 `spring.ai.deepseek.api-key` 替换为实际的 API 密钥即可启动运行。

### 3. 🗄️ 启动 Milvus

Milvus 是一个开源的向量数据库，用于存储和检索高维向量数据。本项目是使用 Docker 来运行 Milvus，当然你也可以选择其他方式安装 Milvus或者使用已经部署好的 Milvus 服务。

> PS: 如果你不运行 spring-ai-rag 模块和 spring-ai-embedding 模块，可以跳过此步骤。

这个项目使用的 milvus 版本是 2.5.0 版本，安装方式见：[Install Milvus in Docker](https://milvus.io/docs/install_standalone-docker.md)。

⚠️本人的电脑是 Mac Air M2 芯片，使用官方文档中的 docker-compose 文件启动 Milvus 时，遇到 `milvus-standalone` 镜像不匹配问题。

### 4. ▶️ 运行示例

完成上述步骤后，你可以选择运行不同的示例模块来体验 Spring AI 的功能。如启动运行 **spring-ai-chat-deepseek** 模块（具体端口可以根据你自己的配置而定）：
```bash
2025-06-04T14:18:43.939+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] c.g.ai.chat.deepseek.DsChatApplication   : Starting DsChatApplication using Java 21.0.2 with PID 88446 (/Users/glmapper/Documents/projects/glmapper/spring-ai-summary/spring-ai-chat/spring-ai-chat-deepseek/target/classes started by glmapper in /Users/glmapper/Documents/projects/glmapper/spring-ai-summary)
2025-06-04T14:18:43.941+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] c.g.ai.chat.deepseek.DsChatApplication   : The following 1 profile is active: "deepseek"
2025-06-04T14:18:44.469+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat initialized with port 8081 (http)
2025-06-04T14:18:44.475+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.apache.catalina.core.StandardService   : Starting service [Tomcat]
2025-06-04T14:18:44.476+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.apache.catalina.core.StandardEngine    : Starting Servlet engine: [Apache Tomcat/10.1.33]
2025-06-04T14:18:44.501+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring embedded WebApplicationContext
2025-06-04T14:18:44.502+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] w.s.c.ServletWebServerApplicationContext : Root WebApplicationContext: initialization completed in 533 ms
2025-06-04T14:18:44.962+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.s.b.a.e.web.EndpointLinksResolver      : Exposing 14 endpoints beneath base path '/actuator'
2025-06-04T14:18:44.988+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] o.s.b.w.embedded.tomcat.TomcatWebServer  : Tomcat started on port 8081 (http) with context path '/'
2025-06-04T14:18:44.997+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] c.g.ai.chat.deepseek.DsChatApplication   : Started DsChatApplication in 1.215 seconds (process running for 1.637)
2025-06-04T14:18:45.175+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [on(2)-127.0.0.1] o.a.c.c.C.[Tomcat].[localhost].[/]       : Initializing Spring DispatcherServlet 'dispatcherServlet'
2025-06-04T14:18:45.175+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [on(2)-127.0.0.1] o.s.web.servlet.DispatcherServlet        : Initializing Servlet 'dispatcherServlet'
2025-06-04T14:18:45.176+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [on(2)-127.0.0.1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```
启动完成后，可以通过 HTTPie 或 Postman 等工具进行测试。
```bash
GET /api/deepseek/chatWithMetric?userInput="你是谁" HTTP/1.1
Host: localhost:8081
User-Agent: HTTPie
```
结果如下：
![chat-ds-metrics.png](docs/statics/chat-ds-metrics.png)

你可以继续使用下面的请求来查看 Token 使用情况：
```bash
# completion tokens
http://localhost:8081/actuator/metrics/ai.completion.tokens
# prompt tokens
http://localhost:8081/actuator/metrics/ai.prompt.tokens
# total tokens
http://localhost:8081/actuator/metrics/ai.total.tokens
```
以 `ai.completion.tokens` 为例，结果如下：
```json
{
   "name": "ai.completion.tokens",
   "measurements": [
      {
         "statistic": "COUNT",
         "value": 34
      }
   ],
   "availableTags": []
}
```

## 📚 模块说明

### 1. 💬 聊天模块 (spring-ai-chat)

提供多种 LLM 模型的接入实现，支持：
- 单模型对话：支持 OpenAI、通义千问、豆包、DeepSeek 等模型
- 多模型并行调用：支持多个模型同时调用，结果对比
- 提示词模板：支持自定义提示词模板和变量替换
- Token 统计：支持输入输出 Token 统计和成本估算

### 2. 📖 RAG 模块 (spring-ai-rag)

实现检索增强生成，包含：
- 文档向量化：支持多种文档格式的向量化处理
- 向量存储：使用 Milvus 进行高效的向量存储和检索
- 语义检索：支持相似度搜索和混合检索
- 问答生成：基于检索结果生成准确的回答

### 3. 🛠️ 工具调用模块 (spring-ai-tool-calling)

展示如何实现工具函数调用：
- 函数定义：使用 @Tool 注解定义工具函数
- 工具注册：支持动态注册和配置工具
- 动态调用：支持运行时动态调用工具
- 结果处理：支持工具调用结果的格式化和处理

### 4. 🧠 会话记忆模块 (spring-ai-chat-memory)

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

## 📝 注意事项

1. **API 密钥安全**
   - 建议使用环境变量存储 API 密钥，避免泄露风险
   - 切勿在代码仓库中硬编码密钥
   - 定期轮换密钥，提升安全性

2. **Milvus 使用**
   - 创建集合时需确保向量维度与 embedding 模型一致
   - 检索前需先加载集合（load collection）
   - 创建索引后再进行检索，提升性能

3. **Token 使用**
   - 持续监控 Token 消耗，避免超额
   - 设置合理的 Token 限制，防止滥用
   - 推荐实现缓存机制，提升响应速度与成本控制

## 📄 License & 说明

* 1、本项目采用 MIT 许可证。详见 [LICENSE](LICENSE) 文件。 另外本项目仅供学习和研究使用，不适用于生产环境，请勿将样例工程直接用于生产环境。使用时请遵守相关模型的使用条款和条件。
* 2、本项目的所有代码和文档均由 [glmapper](https://github.com/glmapper) 独立开发和维护，欢迎大家提出意见和建议，如果对你有帮助，请给个 Star 支持一下哦！如果你有任何问题或建议，请在 GitHub 上提交 Issue 或 PR，或者通过[这里](http://www.glmapper.com/about)联系我。后续我将进一步将关于 **spring ai 的相关技术文章**同步发布到本仓库和个人微信公众号：**磊叔的技术博客**，也欢迎扫码关注。

<p align="center">
  <img src="docs/statics/wx-gzh.png" alt="wx-gzh.png" width="200"/>
</p>

## 🙏 致谢

- [Spring AI](https://github.com/spring-projects/spring-ai) - 提供强大的 AI 集成框架
- [OpenAI](https://openai.com) - 提供 GPT 系列模型
- [通义千问](https://qianwen.aliyun.com) - 提供 Qwen 系列模型
- [豆包](https://www.volcengine.com/docs/82379) - 提供豆包系列模型
- [Milvus](https://milvus.io) - 提供向量数据库支持
