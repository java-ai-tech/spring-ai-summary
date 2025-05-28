# Spring AI Summary

基于 Spring AI 框架的多模型 LLM 应用示例项目，展示如何集成和使用多种大语言模型。

## 1. 项目介绍

### 1.1 项目概述

本项目是一个 Spring AI 学习示例项目，旨在通过实践展示 Spring AI 框架的核心功能和使用方法。项目提供了完整的多模型 LLM 集成方案，包括基础聊天、多模型并行调用、Token 统计、提示词模板等功能。

### 1.2 核心特性

- **多模型支持**：集成 OpenAI、DeepSeek、豆包等多种 LLM 模型
- **并行调用**：支持同时调用多个模型并对比结果
- **提示词工程**：提供模板化提示词管理和参数化配置
- **监控统计**：实时 Token 使用量统计和性能监控
- **向量服务**：文本向量化和语义搜索功能
- **生产就绪**：包含错误处理、重试机制、健康检查等

### 1.3 支持的模型

| 模型提供商 | 模型类型 | 支持功能 |
|-----------|---------|---------|
| OpenAI | GPT-3.5/4 | 聊天、向量化 |
| DeepSeek | DeepSeek-Chat | 聊天 |
| 豆包（火山引擎） | Doubao | 聊天、向量化 |

### 1.4 技术栈

- **核心框架**：Spring Boot 3.3.6 + Spring AI 1.0.0
- **Java 版本**：JDK 21+
- **监控组件**：Micrometer + Spring Boot Actuator
- **构建工具**：Maven 3.6+

## 2. 快速开始

### 2.1 环境准备

#### 系统要求
- JDK 21 或更高版本
- Maven 3.6 或更高版本
- 网络连接（用于访问 LLM API）

#### API 密钥准备
在开始之前，请准备以下 API 密钥：
- OpenAI API Key（可选）
- DeepSeek API Key（可选）
- 豆包 API Key（可选）

### 2.2 项目安装

#### 克隆项目
```bash
git clone https://github.com/glmapper/spring-ai-summary.git
cd spring-ai-summary
```

#### 环境变量配置
```bash
# 必需配置
export OPENAI_API_KEY=your-openai-api-key

# 可选配置
export DEEPSEEK_API_KEY=your-deepseek-api-key
export DOUBAO_API_KEY=your-doubao-api-key
```

#### 配置文件设置
在 `src/main/resources` 目录下创建或修改配置文件：

**application-openai.properties**
```properties
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-3.5-turbo
spring.ai.openai.chat.options.temperature=0.7
```

**application-deepseek.properties**
```properties
spring.ai.deepseek.api-key=${DEEPSEEK_API_KEY}
spring.ai.deepseek.chat.options.model=deepseek-chat
```

### 2.3 运行项目

#### 启动应用
```bash
mvn spring-boot:run
```

#### 验证安装
访问以下端点验证项目是否正常运行：
- 健康检查：`http://localhost:8081/actuator/health`
- 基础聊天：`http://localhost:8081/api/chat/prompt?userInput=Hello`
- 监控指标：`http://localhost:8081/actuator/metrics`

### 2.4 基础使用示例

#### 简单聊天
```bash
curl "http://localhost:8081/api/chat/prompt?userInput=你好，请介绍一下Spring AI"
```

#### 多模型对比
```bash
curl "http://localhost:8081/api/multi-chat/multiClientFlow"
```

#### 文本向量化
```bash
curl "http://localhost:8081/api/embedding?text=Spring AI是一个很棒的框架"
```

## 3. 项目模块结构解释

### 3.1 整体架构设计

项目采用模块化设计，按功能划分为四个核心模块，每个模块职责清晰，便于维护和扩展。

```
spring-ai-summary/
├── src/main/java/com/glmapper/
│   ├── chat/           # 聊天模块
│   ├── embedding/      # 向量服务模块  
│   ├── monitor/        # 监控模块
│   └── config/         # 配置模块
```

### 3.2 聊天模块（chat）

#### 模块职责
负责处理与 LLM 的对话交互，是项目的核心功能模块。

#### 主要组件
- **BasicChatController**：基础聊天功能
  - 单模型调用
  - 简单问答交互
  - 基础错误处理

- **MultiChatController**：多模型聊天
  - 并行调用多个模型
  - 结果对比和聚合
  - 性能对比分析

- **PromptTemplateController**：提示词模板
  - 模板化提示词管理
  - 参数化配置
  - 自定义分隔符支持

#### 核心实现
```java
@RestController
@RequestMapping("/api/chat")
public class BasicChatController {
    
    @Autowired
    private ChatClient chatClient;
    
    @GetMapping("/prompt")
    public String chat(@RequestParam String message) {
        return chatClient.prompt()
                .user(message)
                .call()
                .content();
    }
}
```

### 3.3 向量服务模块（embedding）

#### 模块职责
提供文本向量化服务，支持语义搜索和相似度计算。

#### 主要功能
- **文本向量化**：将文本转换为数值向量
- **相似度计算**：计算文本间的语义相似度
- **向量存储**：支持向量数据持久化
- **语义搜索**：基于向量的语义检索

#### 核心实现
```java
@RestController
@RequestMapping("/api/embedding")
public class EmbeddingController {
    
    @Autowired
    private EmbeddingModel embeddingModel;
    
    @GetMapping
    public List<Double> embed(@RequestParam String text) {
        return embeddingModel.embed(text);
    }
}
```

### 3.4 监控模块（monitor）

#### 模块职责
负责系统监控和指标收集，提供运行时的可见性。

#### 监控指标
- **Token 统计**
  - 输入 Token 数量
  - 输出 Token 数量
  - 总 Token 消耗

- **性能指标**
  - 响应时间
  - 请求频率
  - 错误率统计

- **系统健康**
  - 模型连接状态
  - API 可用性
  - 资源使用情况

#### 核心实现
```java
@Component
public class SimpleMetricAdvisor implements CallAdvisor {
    
    private final MeterRegistry meterRegistry;
    
    @Override
    public AdvisedResponse adviseResponse(AdvisedRequest advisedRequest, 
                                        CallAdvisorChain chain) {
        // Token 统计逻辑
        // 性能监控逻辑
        return chain.nextAdvisor(advisedRequest);
    }
}
```

### 3.5 配置模块（config）

#### 模块职责
管理系统配置，提供灵活的配置管理机制。

#### 配置类型
- **模型配置**：各 LLM 模型的连接参数
- **客户端配置**：HTTP 客户端和连接池设置
- **业务配置**：应用级别的业务参数
- **监控配置**：指标收集和监控设置

#### 核心配置
```java
@Configuration
@EnableConfigurationProperties
public class ChatClientConfig {
    
    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a helpful AI assistant")
                .defaultAdvisors(new SimpleMetricAdvisor())
                .build();
    }
}
```

### 3.6 模块依赖关系

```
chat 模块
 ├── 依赖 config（获取模型配置）
 └── 依赖 monitor（性能监控）

embedding 模块
 ├── 依赖 config（获取向量模型配置）
 └── 依赖 monitor（使用统计）

monitor 模块
 └── 依赖 config（监控配置）

config 模块
 └── 无外部依赖（基础模块）
```

## 4. 注意事项

### 4.1 API 密钥安全

#### 密钥管理最佳实践
- **环境变量存储**：永远不要在代码中硬编码 API 密钥
- **权限最小化**：为不同环境使用不同的密钥
- **定期轮换**：建议每 3-6 个月更换一次密钥
- **访问控制**：限制密钥的访问权限和使用范围

#### 生产环境配置
```bash
# 推荐使用密钥管理服务
export OPENAI_API_KEY=$(vault kv get -field=api_key secret/openai)
```

### 4.2 成本控制

#### Token 使用优化
- **监控消耗**：定期检查 Token 使用量统计
- **设置限制**：配置单次请求的最大 Token 数
- **缓存策略**：对相同请求实现缓存机制
- **模型选择**：根据需求选择合适的模型（成本vs性能）

#### 成本监控配置
```properties
# 设置最大 Token 限制
spring.ai.openai.chat.options.max-tokens=1000

# 启用请求缓存
spring.cache.type=caffeine
```

### 4.3 性能优化

#### 连接池配置
```properties
# HTTP 连接池设置
spring.ai.openai.base-url=https://api.openai.com
spring.ai.openai.chat.options.timeout=30s
```

#### 并发控制
- **限流机制**：防止 API 调用频率过高
- **异步处理**：对于耗时操作使用异步调用
- **连接复用**：合理配置 HTTP 连接池
- **超时设置**：设置合理的请求超时时间

### 4.4 错误处理

#### 常见错误类型
- **API 限流**：429 Too Many Requests
- **认证失败**：401 Unauthorized  
- **网络超时**：连接超时或读取超时
- **模型不可用**：503 Service Unavailable

#### 重试策略
```java
@Retryable(value = {ApiException.class}, maxAttempts = 3)
public String callWithRetry(String prompt) {
    return chatClient.prompt().user(prompt).call().content();
}
```

### 4.5 监控和告警

#### 关键指标监控
- **Token 消耗速率**：防止意外的高消耗
- **API 响应时间**：监控服务性能
- **错误率统计**：及时发现问题
- **模型可用性**：确保服务稳定

#### 告警配置建议
```yaml
management:
  metrics:
    export:
      prometheus:
        enabled: true
  endpoints:
    web:
      exposure:
        include: health,metrics,prometheus
```

## 5. 贡献指南

### 5.1 贡献方式

#### 如何参与贡献
- **报告问题**：通过 GitHub Issues 报告 bug 或提出改进建议
- **功能请求**：提出新功能需求和使用场景
- **代码贡献**：提交 Pull Request 改进代码
- **文档完善**：改进项目文档和示例代码

### 5.2 开发规范

#### 代码规范
- **命名规范**：使用有意义的类名、方法名和变量名
- **注释要求**：为复杂逻辑添加详细注释
- **异常处理**：合理处理异常情况
- **日志记录**：添加适当的日志输出

#### 提交规范
```bash
# 提交信息格式
feat: 添加新的模型支持
fix: 修复Token统计错误
docs: 更新API文档
test: 添加单元测试
```

### 5.3 Pull Request 流程

#### 提交前检查清单
- [ ] 代码符合项目规范
- [ ] 添加必要的单元测试
- [ ] 更新相关文档
- [ ] 本地测试通过
- [ ] 提交信息清晰明确

#### PR 模板
```markdown
## 变更描述
简要描述本次变更的内容和目的

## 变更类型
- [ ] 新功能
- [ ] Bug 修复
- [ ] 文档更新
- [ ] 性能优化

## 测试说明
描述如何测试本次变更

## 相关 Issue
关联的 Issue 编号（如果有）
```

### 5.4 开发环境搭建

#### 本地开发配置
```bash
# 1. Fork 项目到个人仓库
# 2. 克隆到本地
git clone https://github.com/your-username/spring-ai-summary.git

# 3. 创建开发分支
git checkout -b feature/your-feature-name

# 4. 配置开发环境
cp .env.example .env
# 编辑 .env 文件，添加必要的 API 密钥
```

#### 测试运行
```bash
# 运行单元测试
mvn test

# 运行集成测试
mvn verify

# 启动开发服务器
mvn spring-boot:run -Dspring.profiles.active=dev
```

## 6. 参考资料

### 6.1 官方文档

#### Spring AI 相关
- [Spring AI 官方文档](https://docs.spring.io/spring-ai/reference/index.html) - 框架核心文档
- [Spring AI GitHub](https://github.com/spring-projects/spring-ai) - 源码和示例
- [Spring AI API 参考](https://docs.spring.io/spring-ai/docs/current/api/) - API 详细说明

#### Spring 生态
- [Spring Boot 文档](https://docs.spring.io/spring-boot/docs/current/reference/html/) - Spring Boot 官方指南
- [Spring Framework 文档](https://docs.spring.io/spring-framework/docs/current/reference/html/) - Spring 核心框架
- [Micrometer 文档](https://micrometer.io/docs) - 监控指标库

### 6.2 模型提供商文档

#### OpenAI
- [OpenAI API 文档](https://platform.openai.com/docs/api-reference) - API 接口说明
- [OpenAI 模型列表](https://platform.openai.com/docs/models) - 可用模型介绍
- [OpenAI 最佳实践](https://platform.openai.com/docs/guides/production-best-practices) - 生产环境指南

#### DeepSeek
- [DeepSeek API 文档](https://platform.deepseek.com/api-docs/) - API 使用指南
- [DeepSeek 模型介绍](https://platform.deepseek.com/docs) - 模型能力说明

#### 豆包（火山引擎）
- [火山引擎 API 文档](https://www.volcengine.com/docs/82379) - 豆包 API 文档
- [模型使用指南](https://www.volcengine.com/docs/82379/1099475) - 使用说明

### 6.3 学习资源

#### 技术博客
- [Spring AI 入门教程](https://spring.io/blog/category/spring-ai) - Spring 官方博客
- [LLM 集成最佳实践](https://www.baeldung.com/spring-ai) - Baeldung 教程系列
- [Java AI 开发指南](https://foojay.io/today/category/artificial-intelligence/) - Java AI 相关文章

#### 社区资源
- [Spring AI 讨论区](https://github.com/spring-projects/spring-ai/discussions) - 社区讨论
- [Stack Overflow](https://stackoverflow.com/questions/tagged/spring-ai) - 问题解答
- [Reddit Spring 社区](https://www.reddit.com/r/springframework/) - 开发者交流

### 6.4 相关项目

#### 示例项目
- [Spring AI Examples](https://github.com/spring-projects/spring-ai-examples) - 官方示例
- [Awesome Spring AI](https://github.com/spring-projects/awesome-spring-ai) - 精选资源列表
- [Spring AI Samples](https://github.com/spring-tips/spring-ai) - Spring Tips 示例

#### 工具和库
- [LangChain4j](https://github.com/langchain4j/langchain4j) - Java LLM 框架
- [OpenAI Java](https://github.com/TheoKanning/openai-java) - OpenAI Java 客户端
- [Semantic Kernel Java](https://github.com/microsoft/semantic-kernel) - 微软 AI 框架

---

## 许可证

本项目采用 MIT 许可证，详见 [LICENSE](LICENSE) 文件。

## 联系方式

- 项目维护者：glmapper
- GitHub：[https://github.com/glmapper/spring-ai-summary](https://github.com/glmapper/spring-ai-summary)
- 问题反馈：[GitHub Issues](https://github.com/glmapper/spring-ai-summary/issues)
