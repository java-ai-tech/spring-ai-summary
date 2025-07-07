## 🚀 Spring AI Summary

<p align="left">
  <a href="README.md" target="_blank"><img src="https://img.shields.io/badge/lang-中文-red?logo=googletranslate" alt="中文" style="vertical-align:middle; margin-right:4px;"/></a>
  <a href="README_EN.md" target="_blank"><img src="https://img.shields.io/badge/lang-English-blue?logo=googletranslate" alt="English" style="vertical-align:middle; margin-right:4px;"/></a>
  <a href="https://github.com/java-ai-tech/spring-ai-summary/wiki" target="_blank"><img src="https://img.shields.io/badge/doc-wiki-blue?logo=readthedocs" alt="document" style="vertical-align:middle; margin-right:4px;"/></a>
  <img src="https://img.shields.io/badge/spring--ai--summary-v1.0.0-blue.svg" alt="Spring AI Summary" style="vertical-align:middle; margin-right:4px;"/>
  <img src="https://visitor-badge.laobi.icu/badge?page_id=java-ai-tech.spring-ai-summary" alt="Visitors" style="vertical-align:middle; margin-right:4px;"/>
</p>

**Spring AI Summary** is a modular collection of sample projects based on native **Spring AI** framework, helping developers quickly master Spring AI's core features through clear code examples and detailed documentation.

### 👥 Target Audience
Spring AI Summary is designed for developers interested in the Spring AI framework. Whether you're a beginner or experienced engineer, you can quickly understand the framework's core features and apply them to real projects through this project. With Spring AI Summary, you can:

- Master Spring AI's core concepts and features
- Learn how to build efficient AI applications
- Get the latest technical insights and practical experience

**Join Community** 🎯 Scan the QR code to add the group owner on WeChat (note: Spring AI), let's explore the infinite possibilities of Spring AI together!

<p align="center">
  <img width="189" alt="Community Chat Group" src="docs/statics/my_chat.png" />
</p>

## 📖 Learning Path

If you're new to Spring AI, we recommend starting with the [Spring AI Official Documentation](https://spring.io/projects/spring-ai) to understand the framework's basic concepts and usage. Then you can practice through the various modules in this project to gradually deepen your understanding of the framework's core features.

**Recommended Learning Sequence**:
1. 📚 [Spring AI Official Documentation](https://spring.io/projects/spring-ai) - Understand basic concepts
2. 💬 **spring-ai-chat** - Chat application development (essential starting point)
3. 🔧 **spring-ai-tool-calling** - Tool calling capabilities
4. 🧠 **spring-ai-vector** - Vector database integration
5. 🚀 **MCP/RAG/AGENT** - Advanced application patterns

👇Let's start the Spring AI journey below～

## 🚀 Quick Start

### ⚙️ Environment Requirements

| Dependency     | Version/Requirement           | Note                        |
| -------------- | ----------------------------- | --------------------------- |
| SpringBoot     | 3.3.6                         | -                           |
| Spring AI      | 1.0.0                         | -                           |
| JDK            | 21+                           | -                           |
| Maven          | 3.6+                          | Strongly recommend using mvnd instead of mvn |
| Docker         | (for running Milvus, Nacos, etc.) |                        |

### 1. 🧬 Clone Project

```bash
# Clone project to local
git clone https://github.com/java-ai-tech/spring-ai-summary.git
# Enter project directory and compile
cd spring-ai-summary && mvn clean compile -DskipTests
```

> If you encounter slow Maven dependency downloads, try using domestic Maven mirror sources like Aliyun, Tsinghua University, etc. If you have any other issues during runtime, you can scan the QR code to join the WeChat group above for consultation and discussion～～～

### 2. 🛠️ Configure Environment Variables

For the `application.yml`/`application.properties` files in each module's resource folder, configure the corresponding API keys according to your needs. For example, the **spring-ai-chat-deepseek** module:

```properties
# because we do not use the OpenAI protocol
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.deepseek.base-url=https://api.deepseek.com
spring.ai.deepseek.chat.completions-path=/v1/chat/completions
spring.ai.deepseek.chat.options.model=deepseek-chat
```
Replace your `spring.ai.deepseek.api-key` with the actual API key to start running. For how to apply for API keys, you can visit the project [Wiki page](https://github.com/java-ai-tech/spring-ai-summary/wiki).

> 💡 **Security Tip**: Use environment variables to store API keys to avoid code leakage. [API Key Application Guide](https://github.com/java-ai-tech/spring-ai-summary/wiki)

### 3. ▶️ Run Examples

After completing the above steps, you can choose to run different example modules to experience Spring AI features. For example, start running the **spring-ai-chat-deepseek** module (specific port may vary according to your configuration):

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
2025-06-04T14:18:45.176+08:00  INFO 88446 --- [spring-ai-chat-deepseek] [on(2)-127.0.1] o.s.web.servlet.DispatcherServlet        : Completed initialization in 1 ms
```
If you see similar startup logs as above, congratulations, you've started successfully 🎉🎉🎉🎉🎉....! You've opened the door to Spring AI, and you can now start exploring various functional modules. After startup, you can test using cUrl, HTTPie, or Postman and other tools.

```bash
curl localhost:8081/api/deepseek/chatWithMetric?userInput="Who are you?"
```
Results as follows:

**Success Indicator** 🎉: Seeing similar responses indicates successful operation!

![Running Results](docs/statics/chat-ds-metrics.png)

You can continue using the following requests to check Token usage:

```bash
# completion tokens
http://localhost:8081/actuator/metrics/ai.completion.tokens
# prompt tokens
http://localhost:8081/actuator/metrics/ai.prompt.tokens
# total tokens
http://localhost:8081/actuator/metrics/ai.total.tokens
```
Taking `ai.completion.tokens` as an example, the results are as follows:

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

**For usage methods and configurations of other modules, please check the [Wiki page](https://github.com/java-ai-tech/spring-ai-summary/wiki) or each module's `README.md` file.**

## 📚 Learning Resources (Continuously Updated)

Here are some recommended learning resources:

> The official also has a [learning resource collection](https://github.com/spring-ai-community/awesome-spring-ai), but it mainly aggregates overseas materials, so this project focuses more on aggregating domestic learning resources for your reference.

#### Technical Community

- [Spring AI Official Documentation](https://spring.io/projects/spring-ai)
- [Spring AI Alibaba Official Documentation](https://github.com/alibaba/spring-ai-alibaba)

#### Project Series
- [MindMark is a RAG system based on SpringAI](https://gitee.com/mumu-osc/mind-mark)
- [My AI Agent is an intelligent agent service based on Spring Boot and Spring AI framework](https://github.com/Cunninger/my-ai-agent)

#### Blog Series
- [MaJiang's Journal--Spring AI Series Column](https://cloud.tencent.com/developer/column/72423) Because the author didn't manage the column, this links to the homepage; Additionally, this series of articles is excellent for learning Spring AI's design philosophy and implementation methods, but it's based on the M series version, so some content may be inconsistent with the latest version.
- [In-depth Analysis of Spring AI Series](https://www.cnblogs.com/guoxiaoyu/p/18666904) 
- [How to Build MCP Client-Server Architecture with Spring AI](https://spring.didispace.com/article/spring-ai-mcp.html)
- [Building Effective Agents with Spring AI](https://spring.io/blog/2025/01/21/spring-ai-agentic-patterns) 🌟🌟🌟🌟🌟
- [Spring AI Large Model Return Content Formatting Source Code Analysis and Simple Usage](https://juejin.cn/post/7378696051082199080)
- [Spring AI EmbeddingModel Concept and Source Code Analysis](https://my.oschina.net/u/2391658/blog/18534829)
- [Complete RAG Techniques: Simpler, More Practical Implementation Methods ✨](https://www.readme-i18n.com/FareedKhan-dev/all-rag-techniques?lang=zh)
- [Spring AI Framework Principles and Practice](https://juejin.cn/column/7375109287716372520) 🌟🌟🌟

#### Video Series
- [How to Build Agents with Spring AI](https://www.youtube.com/watch?v=d7m6nJxfi0g)
- [Spring AI Video Tutorial Series](https://www.youtube.com/watch?v=yyvjT0v3lpY&list=PLZV0a2jwt22uoDm3LNDFvN6i2cAVU_HTH)
- [Mark's Tech Workshop](https://space.bilibili.com/1815948385) 🌟🌟🌟🌟🌟

If you have good articles or resources, you're also welcome to submit PRs or Issues for supplementation and improvement. Below is the development and contribution guide.

## 🔧 Development Guide (Code, Documentation)

First, we warmly welcome any form of contribution, including but not limited to **code, documentation, testing, code comments**, etc. But please follow the process below before submitting contributions:

### Contributing Code

1. **Fork Project**

As shown in the figure below, fork the repository to your personal repository
![how-to-fork.png](docs/statics/how-to-fork.png)

2. **Clone Your Personal Repository Code**

   ```bash
   # Fork the project on GitHub
   # Clone your fork repository
   git clone https://github.com/your-username/spring-ai-summary.git
   cd spring-ai-summary
   ```

3. **Create Feature Branch**
   ```bash
   # Create and switch to new feature branch
   git checkout -b feature/your-feature-name
   ```

4. **Development Standards**
   - Follow the project's code style and naming conventions
   - Ensure code passes all tests (if any)
   - Update relevant documentation

5. **Submit Code**
   ```bash
   # Add modified files
   git add .
   # Commit code
   git commit -m "feat: add new feature"
   # Push to your fork repository
   git push origin feature/your-feature-name
   ```
   
6. **Create Pull Request**
   - Create Pull Request on GitHub
   - Fill in PR description, explaining changes and reasons
   - Wait for code review and merge

## 📝 Important Notes

1. **API Key Security**
   - Recommend using environment variables to store API keys to avoid leakage risks
   - Never hardcode keys in code repositories
   - Regularly rotate keys to enhance security

2. **Token Usage**
   - Continuously monitor Token consumption to avoid overuse
   - Set reasonable Token limits to prevent abuse
   - Recommend implementing caching mechanisms to improve response speed and cost control

## 📄 License & Description

* 1. This project adopts the MIT license. See [LICENSE](LICENSE) file for details. Additionally, this project is only for learning and research use, not suitable for production environments. Please do not use sample projects directly in production environments. Please comply with the terms and conditions of relevant models when using.
* 2. All code and documentation in this project are independently developed and maintained by [glmapper](https://github.com/glmapper). Everyone is welcome to provide opinions and suggestions. If this helps you, please give a Star to support! If you have any questions or suggestions, please submit Issues or PRs on GitHub, or contact me through [here](http://www.glmapper.com/about). I will further synchronize **Spring AI related technical articles** to this repository and my personal WeChat public account: **磊叔的技术博客**. You're also welcome to scan the code to follow.

<p align="center">
  <img src="docs/statics/wx-gzh.png" alt="WeChat Public Account" width="200"/>
</p>

## 🙏 Acknowledgments

- [Spring AI](https://github.com/spring-projects/spring-ai) - Provides powerful AI integration framework
- [OpenAI](https://openai.com) - Provides GPT series models
- [Qwen](https://qianwen.aliyun.com) - Provides Qwen series models
- [Doubao](https://www.volcengine.com/docs/82379) - Provides Doubao series models
- [Milvus](https://milvus.io) - Provides vector database support

This project is a completely open source project, with the main purpose of gathering more high-quality Spring AI related learning resources. Of course, **related learning resources mainly come from the internet, if there is any infringement, please contact for deletion!!!** Here we also express heartfelt thanks to all friends who participate in open source contributions and share technology in the technical community!

## Star History

[![Star History Chart](https://api.star-history.com/svg?repos=java-ai-tech/spring-ai-summary&type=Date)](https://www.star-history.com/#java-ai-tech/spring-ai-summary&Date)