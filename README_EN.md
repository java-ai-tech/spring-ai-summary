# Spring AI Summary

![Spring AI Summary](https://img.shields.io/badge/spring--ai--summary-v1.0.0-blue.svg)

<p align="left">
  <a href="README.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;margin-right:10px;">🇨🇳 中文</span></a>
  <a href="README_EN.md" style="text-decoration:none;"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;">🇺🇸 English</span></a>
  <a href="https://github.com/java-ai-tech/spring-ai-summary/wiki" target="_blank"><span style="display:inline-block;border:1px solid #ccc;border-radius:4px;padding:2px 10px;">📖 Wiki</span></a>
</p>

🚀🚀🚀 Spring AI Summary is a collection of sample projects based on native Spring AI, designed to help developers quickly master the core features and usage of the Spring AI framework. With a modular design, each module focuses on a specific functional area, providing clear code examples and detailed documentation to help you get started easily and deeply understand the core concepts of the framework.

### Project Features

- **Modular Design**: Each module focuses on a functional area, such as chat, RAG (Retrieval Augmented Generation), text embedding, tool function calling, chat memory management, etc., making it easy for developers to learn and apply as needed.
- **Practical Examples**: Each module contains complete sample code and documentation, demonstrating real-world application scenarios of Spring AI, helping you quickly build your own AI applications.
- **Continuous Updates**: The project keeps up with the latest developments and version updates of Spring AI, optimizing sample code and documentation in a timely manner to ensure content is always up-to-date.
- **Community Support**: High-quality technical articles and practical experience are shared, offering best practices to help developers better understand and apply Spring AI.

### Who Is This For?

Spring AI Summary is for developers interested in the Spring AI framework. Whether you are a beginner or an experienced engineer, you can quickly learn the core features of the framework and apply them to real projects through this project.

With Spring AI Summary, you can:

- Master the core concepts and features of Spring AI.
- Learn how to build efficient AI applications.
- Get the latest technical trends and practical experience.

Welcome to join the community and explore the infinite possibilities of Spring AI together!

<p align="center">
  <img width="189" alt="image" src="docs/statics/my_chat.png" />
</p>

## 🗂️ Project Structure

This project adopts a modular design, mainly divided into the following modules by feature:

```
spring-ai-summary/
├── spring-ai-chat/                   # Chat module
│   ├── spring-ai-chat-openai/        # OpenAI integration
│   ├── spring-ai-chat-qwen/          # Qwen integration
│   ├── spring-ai-chat-doubao/        # Doubao integration
│   ├── spring-ai-chat-deepseek/      # DeepSeek integration
│   ├── spring-ai-chat-multi/         # Multi chat model
│   │   spring-ai-chat-ollama/        # Ollama integration
│   └── spring-ai-chat-multi-openai/  # Multi OpenAI protocol models
├── spring-ai-rag/                    # RAG (Retrieval Augmented Generation)
├── spring-ai-vector/                 # Text embedding service
│   ├── spring-ai-vector-milvus/      # Milvus vector storage
│   ├── spring-ai-vector-redis/       # Redis vector storage
├── spring-ai-tool-calling/           # Tool/function calling examples
├── spring-ai-chat-memory/            # Chat memory management
│   ├── spring-ai-chat-memory-jdbc    # JDBC-based storage
│   ├── spring-ai-chat-memory-local   # In-memory storage
├── spring-ai-evaluation/             # AI answer evaluation
└── spring-ai-mcp/                    # MCP examples
    ├── spring-ai-mcp-server          # MCP server
    ├── spring-ai-mcp-client          # MCP client
└── spring-ai-agent/                  # Agent examples
```

## 🚀 Quick Start

### ⚙️ Requirements

| Dependency     | Version/Requirement | Note                |
| -------------- | ------------------ | ------------------- |
| SpringBoot     | 3.3.6              |                     |
| Spring AI      | 1.0.0              |                     |
| JDK            | 21+                |                     |
| Maven          | 3.6+               |                     |
| Docker         | (for running Milvus)|                     |

### 1. 🧬 Clone the Project

```bash
# Clone the project
git clone https://github.com/java-ai-tech/spring-ai-summary.git
# Enter the project directory and compile
cd spring-ai-summary && mvn clean compile -DskipTests
```

> If you encounter slow Maven dependency downloads, try using a domestic Maven mirror (e.g., Aliyun, Tsinghua). For any other issues, feel free to join the WeChat group above for discussion and support.

### 2. 🛠️ Configure Environment Variables

For each module, configure the required API keys in the `application.yml`/`application.properties` file under the `resources` folder. For example, in **spring-ai-chat-deepseek**:

```properties
# because we do not use the OpenAI protocol
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.deepseek.base-url=https://api.deepseek.com
spring.ai.deepseek.chat.completions-path=/v1/chat/completions
spring.ai.deepseek.chat.options.model=deepseek-chat
```
Replace `spring.ai.deepseek.api-key` with your actual API key to start the service. For how to apply for an API key, see the project [Wiki page](https://github.com/java-ai-tech/spring-ai-summary/wiki).

### 3. ▶️ Run Examples

After the above steps, you can run different modules to experience Spring AI features. For example, to start **spring-ai-chat-deepseek** (port may vary):

```bash
2025-06-04T14:18:43.939+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [           main] c.g.ai.chat.deepseek.DsChatApplication   : Starting DsChatApplication using Java 21.0.2 with PID 88446 (/Users/glmapper/Documents/projects/glmapper/spring-ai-summary/spring-ai-chat/spring-ai-chat-deepseek/target/classes started by glmapper in /Users/glmapper/Documents/projects/glmapper/spring-ai-summary)
...
```
Once started, you can test with cUrl, HTTPie, or Postman:

```bash
curl localhost:8081/api/deepseek/chatWithMetric?userInput="Who are you?"
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

**For usage and configuration of other modules, see the [Wiki page](https://github.com/java-ai-tech/spring-ai-summary/wiki) or each module's `README.md`.**

## 📚 Learning Resources (Continuously Updated)

Here are some recommended learning resources:

> The official [Awesome Spring AI](https://github.com/spring-ai-community/awesome-spring-ai) list is also available, but it mainly collects overseas resources. This project focuses on aggregating domestic learning resources for your reference.

#### Technical Community

- [Spring AI Official Documentation](https://spring.io/projects/spring-ai)
- [Spring AI Alibaba Official Documentation](https://github.com/alibaba/spring-ai-alibaba)

#### Project Series
- [MindMark: A RAG system based on SpringAI](https://gitee.com/mumu-osc/mind-mark)
- [My AI Agent: An intelligent agent service based on Spring Boot and Spring AI](https://github.com/Cunninger/my-ai-agent)

#### Blog Series
- [MaJiang's Spring AI Series](https://cloud.tencent.com/developer/column/72423) (Chinese, some content may be outdated)
- [In-depth Spring AI Series](https://www.cnblogs.com/guoxiaoyu/p/18666904) (Chinese, discontinued)
- [How to Build MCP Client-Server Architecture with Spring AI](https://spring.didispace.com/article/spring-ai-mcp.html)
- [Building Effective Agents with Spring AI](https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns)
- [Spring AI Large Model Output Formatting and Simple Usage](https://juejin.cn/post/7378696051082199080)
- [Spring AI EmbeddingModel Concept and Source Code Analysis](https://my.oschina.net/u/2391658/blog/18534829)

#### Video Series
- [How to Build Agents with Spring AI (YouTube)](https://www.youtube.com/watch?v=d7m6nJxfi0g)
- [Spring AI Video Tutorials (YouTube)](https://www.youtube.com/watch?v=yyvjT0v3lpY&list=PLZV0a2jwt22uoDm3LNDFvN6i2cAVU_HTH)

If you have good articles or resources, feel free to submit a PR or Issue to supplement and improve this list. See below for development and contribution guidelines.

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
   # Create and switch to a new feature branch
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

## 📝 Notes

1. **API Key Security**
   - Use environment variables to store API keys to avoid leaks
   - Never hardcode keys in the codebase
   - Rotate keys regularly for better security

2. **Token Usage**
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

This project is fully open source, aiming to aggregate more high-quality Spring AI learning resources. Most resources are collected from the internet. If there is any infringement, please contact for removal. Special thanks to all open source contributors and everyone who shares technology in the community!


## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=java-ai-tech/spring-ai-summary&type=Date)](https://www.star-history.com/#java-ai-tech/spring-ai-summary&Date)