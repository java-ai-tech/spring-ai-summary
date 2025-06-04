# Spring AI Summary

![Spring AI Summary](https://img.shields.io/badge/spring--ai--summary-v1.0.0-blue.svg)

<p align="left">
  <a href="README.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;margin-right:10px;">🇨🇳 中文</span></a>
  <a href="README_EN.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;">🇺🇸 English</span></a>
</p>


🚀🚀🚀 This project is a quick-start sample for Spring AI, designed to demonstrate the core features and usage of the Spring AI framework through practical mini-cases. The project adopts a modular design, with each module focusing on a specific functional area for easy learning and extension.

## 📖 About Spring AI

The goal of the Spring AI project is to simplify the development of applications that integrate AI capabilities, avoiding unnecessary complexity. For more information, please visit the [Spring AI official documentation](https://spring.io/projects/spring-ai).

## 🗂️ Project Structure

This project uses a modular design, divided by feature as follows:

```
spring-ai-summary/
├── spring-ai-chat/                   # Chat module
│   ├── spring-ai-chat-openai/        # OpenAI integration
│   ├── spring-ai-chat-qwen/          # Qwen integration
│   ├── spring-ai-chat-doubao/        # Doubao integration
│   ├── spring-ai-chat-deepseek/      # DeepSeek integration
│   ├── spring-ai-chat-multi/         # Multi-model parallel
│   └── spring-ai-chat-multi-openai/  # OpenAI multi-model parallel
├── spring-ai-rag/                    # RAG (Retrieval Augmented Generation)
├── spring-ai-embedding/              # Text embedding service
├── spring-ai-tool-calling/           # Tool/function calling examples
├── spring-ai-chat-memory/            # Chat memory management
├── spring-ai-evaluation/             # AI answer evaluation
└── spring-ai-mcp/                    # MCP examples
```

**The documentation list for different project modules is as follows.：**

* **spring-ai-chat-chat module**
   * [spring-ai-chat-openai](spring-ai-chat/spring-ai-chat-openai/README.md) - OpenAI Model access
   * [spring-ai-chat-qwen](spring-ai-chat/spring-ai-chat-qwen/README.md) - Qwen Model access
   * [spring-ai-chat-doubao](spring-ai-chat/spring-ai-chat-doubao/README.md) - Doubao Model access
   * [spring-ai-chat-deepseek](spring-ai-chat/spring-ai-chat-deepseek/README.md) - DeepSeek Model access
   * [spring-ai-chat-multi](spring-ai-chat/spring-ai-chat-multi/README.md) - multi chat Model access
   * [spring-ai-chat-multi-openai](spring-ai-chat/spring-ai-chat-multi-openai/README.md) - multi OpenAI protocol Model access
* **[spring-ai-embedding-文本向量化服务]()** --to be added
* **[spring-ai-rag-RAG 检索增强生成]()** --to be added
* **[spring-ai-tool-calling-工具函数调用示例]()** --to be added
* **[spring-ai-chat-memory-会话记忆管理]()** --to be added
* **[spring-ai-mcp-MCP 示例]()** --to be added
* **[spring-ai-evaluation-AI 回答评估]()** --to be added
* 
## 🧩 Core Features

This sample project implements the following core features:

- **Multi-model support**: Integrates OpenAI, Qwen, Doubao, DeepSeek, and more LLMs
- **RAG implementation**: Complete retrieval-augmented generation, supporting document embedding and semantic search
- **Function Calling**: Supports function calling and tool integration
- **Chat Memory**: Multiple storage options for chat history
- **Evaluation System**: AI answer quality evaluation tools
- **Monitoring & Stats**: Token usage and performance monitoring

You can quickly get started by following the steps below.

## 🚀 Quick Start

### ⚙️ Requirements

- SpringBoot 3.3.6
- Spring AI 1.0.0
- JDK 21+
- Maven 3.6+
- Docker (for running Milvus)

### 1. 🧬 Clone the Project

```bash
git clone https://github.com/glmapper/spring-ai-summary.git
cd spring-ai-summary
```

### 2. 🛠️ Configure Environment Variables

For each module, configure the required API keys in the `application.yml`/`application.properties` file under the `resources` folder. For example, in **spring-ai-chat-deepseek**:
```properties
# because we do not use the OpenAI protocol
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.deepseek.base-url=https://api.deepseek.com
spring.ai.deepseek.chat.completions-path=/v1/chat/completions
spring.ai.deepseek.chat.options.model=deepseek-chat
```
Replace `spring.ai.deepseek.api-key` with your actual API key to start the service.

### 3. 🗄️ Start Milvus

Milvus is an open-source vector database for storing and retrieving high-dimensional vector data. This project uses Docker to run Milvus, but you can use other installation methods or an existing Milvus service.

> PS: If you do not use the spring-ai-rag or spring-ai-embedding modules, you can skip this step.

The project uses Milvus version 2.5.0. See the [Install Milvus in Docker](https://milvus.io/docs/install_standalone-docker.md) guide.

⚠️ On Mac Air M2, there may be issues with the `milvus-standalone` image when using the official docker-compose file.

### 4. ▶️ Run Examples

After the above steps, you can run different modules to experience Spring AI features. For example, to start **spring-ai-chat-deepseek** (port may vary):
```bash
2025-06-04T14:18:43.939+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] c.g.ai.chat.deepseek.DsChatApplication   : Starting DsChatApplication using Java 21.0.2 with PID 88446 (/Users/glmapper/Documents/projects/glmapper/spring-ai-summary/spring-ai-chat/spring-ai-chat-deepseek/target/classes started by glmapper in /Users/glmapper/Documents/projects/glmapper/spring-ai-summary)
...
```
Once started, you can test with HTTPie or Postman:
```bash
GET /api/deepseek/chatWithMetric?userInput="Who are you?" HTTP/1.1
Host: localhost:8081
User-Agent: HTTPie
```
Result:
![chat-ds-metrics.png](docs/statics/chat-ds-metrics.png)

You can also check token usage:
```bash
# completion tokens
http://localhost:8081/actuator/metrics/ai.completion.tokens
# prompt tokens
http://localhost:8081/actuator/metrics/ai.prompt.tokens
# total tokens
http://localhost:8081/actuator/metrics/ai.total.tokens
```
Example response for `ai.completion.tokens`:
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

## 📚 Module Overview

### 1. 💬 Chat Module (spring-ai-chat)

Provides integration with multiple LLMs:
- Single-model chat: OpenAI, Qwen, Doubao, DeepSeek, etc.
- Multi-model parallel: Call multiple models and compare results
- Prompt templates: Customizable prompt templates and variable replacement
- Token stats: Input/output token statistics and cost estimation

### 2. 📖 RAG Module (spring-ai-rag)

Implements retrieval-augmented generation:
- Document embedding: Supports various document formats
- Vector storage: Efficient storage and retrieval with Milvus
- Semantic search: Similarity and hybrid search
- Answer generation: Generate accurate answers based on retrieval

### 3. 🛠️ Tool Calling Module (spring-ai-tool-calling)

Demonstrates tool/function calling:
- Function definition: Use @Tool annotation to define tools
- Tool registration: Dynamic registration and configuration
- Dynamic invocation: Call tools at runtime
- Result handling: Format and process tool results

### 4. 🧠 Chat Memory Module (spring-ai-chat-memory)

Provides chat history management:
- JDBC persistence: Store chat history in a database
- Local file storage: Store chat history in the file system
- Context management: Manage and clean up chat context

## 🔧 Development Guide

### Contributing

1. **Fork the project**
   ```bash
   # Fork on GitHub
   # Clone your fork
   git clone https://github.com/your-username/spring-ai-summary.git
   cd spring-ai-summary
   ```

2. **Create a feature branch**
   ```bash
   git checkout -b feature/your-feature-name
   ```

3. **Development standards**
   - Follow the project's code style and naming conventions
   - Ensure all tests pass
   - Add necessary unit tests
   - Update relevant documentation
   - Use [Conventional Commits](https://www.conventionalcommits.org/) for commit messages

4. **Commit your code**
   ```bash
   git add .
   git commit -m "feat: add new feature"
   git push origin feature/your-feature-name
   ```

5. **Create a Pull Request**
   - Create a PR on GitHub
   - Describe your changes and reasons
   - Wait for review and merge

### Development Environment Setup
1. **IDE Setup**
   - Recommend IntelliJ IDEA
   - Install Lombok plugin
   - Configure Java 21 SDK
2. **Maven Setup**
   ```xml
   <properties>
       <java.version>21</java.version>
       <spring-ai.version>1.0.0</spring-ai.version>
   </properties>
   ```
3. **Run Tests**
   ```bash
   # Run all tests
   mvn test
   # Run tests for a specific module
   mvn test -pl spring-ai-chat
   ```

## 📝 Notes

1. **API Key Security**
   - Use environment variables to store API keys to avoid leaks
   - Never hardcode keys in the codebase
   - Rotate keys regularly for better security

2. **Milvus Usage**
   - Ensure vector dimension matches the embedding model when creating collections
   - Load the collection before retrieval (load collection)
   - Create indexes before retrieval for better performance

3. **Token Usage**
   - Monitor token consumption to avoid overuse
   - Set reasonable token limits to prevent abuse
   - Implement caching to improve response speed and cost control

## 📄 License & Notice

* 1. This project is licensed under the MIT License. See [LICENSE](LICENSE) for details. This project is for learning and research only, not for production use. Please comply with the terms and conditions of the models you use.
* 2. All code and documentation are independently developed and maintained by [glmapper](https://github.com/glmapper). Feedback and suggestions are welcome! If you find this project helpful, please give it a Star. For questions or suggestions, submit an Issue or PR on GitHub, or contact me via [here](http://www.glmapper.com/about). More Spring AI technical articles will be published in this repo and on my WeChat public account: **磊叔的技术博客** (scan below to follow).

<p align="center">
  <img src="docs/statics/wx-gzh.png" alt="wx-gzh.png" width="200"/>
</p>

## 🙏 Acknowledgements

- [Spring AI](https://github.com/spring-projects/spring-ai) - Powerful AI integration framework
- [OpenAI](https://openai.com) - GPT series models
- [Qwen](https://qianwen.aliyun.com) - Qwen series models
- [Doubao](https://www.volcengine.com/docs/82379) - Doubao series models
- [Milvus](https://milvus.io) - Vector database support
