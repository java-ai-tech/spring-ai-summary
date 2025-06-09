# Spring AI Chat Memory 实战指南：Local 与 JDBC 存储全面解析

在构建智能对话系统时，保持对话上下文的连贯性是提升用户体验的关键。Spring AI 框架提供了强大的 Chat Memory 机制，支持多种存储方式来持久化对话历史。本文将深入解析 Spring AI Chat Memory 的核心机制，并通过实际代码演示如何实现基于本地内存（Local）和数据库（JDBC）的两种存储方案。

## Spring AI Chat Memory 核心机制

### 架构概览 Architecture Overview

Spring AI Chat Memory 采用分层架构设计：

```
┌─────────────────────────────────────┐
│           ChatClient Layer          │
├─────────────────────────────────────┤
│         ChatMemory Advisor          │
├─────────────────────────────────────┤
│         ChatMemory Interface        │
├─────────────────────────────────────┤
│      ChatMemoryRepository Layer     │
├─────────────────────────────────────┤
│    Storage Layer (Local/JDBC)       │
└─────────────────────────────────────┘
```

### 核心组件解析

1. **ChatMemory 接口**：提供统一的对话记忆管理抽象
2. **ChatMemoryRepository**：负责底层存储操作
3. **MessageChatMemoryAdvisor**：基于 Advisor 模式的透明化处理
4. **MessageWindowChatMemory**：支持消息窗口限制的实现

## 实现方案一：Local Memory (本地内存存储)

### 依赖配置

```xml
<!-- pom.xml -->
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
```

### Step 1: 创建 ChatClient 配置

```java
@Configuration
public class ChatClientConfigs {

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, ChatMemory chatMemory) {
        return ChatClient.builder(chatModel)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
                .build();
    }
}
```

**关键点解析**：

- `MessageChatMemoryAdvisor`：采用 Advisor 模式，自动处理消息的存储和检索
- `defaultAdvisors`：为 ChatClient 配置默认的 advisor，使 memory 功能透明化

### Step 2: 实现 ChatMemoryService

```java
@Service
public class ChatMemoryService {
    // 模拟一个会话 ID
    private static final String CONVERSATION_ID = "naming-20250528";

    @Autowired
    private ChatClient chatClient;

    /**
     * 基于 Advisor 模式的聊天方法
     * ChatClient 会自动处理消息的存储和检索
     * 
     * @param message 用户输入消息
     * @param conversationId 对话会话ID，如果为null则使用默认ID
     * @return AI的响应内容
     */
    public String chat(String message, String conversationId) {
        String answer = this.chatClient.prompt()
                .user(message)
                // 关键：通过 advisor 参数指定对话ID
                .advisors(a -> a.param(ChatMemory.CONVERSATION_ID, conversationId == null ? CONVERSATION_ID : conversationId))
                .call()
                .content();
        return answer;
    }
}
```

**核心机制**：

- 通过 `ChatMemory.CONVERSATION_ID` 参数指定对话会话 ID
- ChatClient 自动从 memory 中检索历史消息并添加到 prompt 中
- 响应后自动将对话记录存储到 memory 中

### Step 3: 配置应用属性

```properties
# application.properties
spring.application.name=spring-ai-chat-memory-local
server.port=8083
spring.profiles.active=deepseek

# DeepSeek API 配置
spring.ai.openai.api-key=${spring.ai.openai.api-key}
spring.ai.openai.chat.base-url=https://api.deepseek.com
spring.ai.openai.chat.completions-path=/v1/chat/completions
spring.ai.openai.chat.options.model=deepseek-chat
```

### Local Memory 优缺点

**优点**：
- 配置简单，开箱即用
- 响应速度快，无网络延迟
- 适合开发和测试环境

**缺点**：
- 数据不持久化，重启后丢失
- 不支持多实例间共享
- 内存使用量随对话量增长

## 实现方案二：JDBC Memory (数据库存储)

### 依赖配置

```xml
<!-- pom.xml -->
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-chat-memory-repository-jdbc</artifactId>
    </dependency>
    <dependency>
        <groupId>mysql</groupId>
        <artifactId>mysql-connector-java</artifactId>
        <version>8.0.33</version>
    </dependency>
</dependencies>
```

### Step 1: 数据库表结构

```sql
-- schema-mysql.sql
CREATE TABLE IF NOT EXISTS SPRING_AI_CHAT_MEMORY (
    conversation_id VARCHAR(36) NOT NULL,
    content TEXT NOT NULL,
    type VARCHAR(10) NOT NULL,
    `timestamp` TIMESTAMP NOT NULL,
    CONSTRAINT TYPE_CHECK CHECK (type IN ('USER', 'ASSISTANT', 'SYSTEM', 'TOOL'))
);

CREATE INDEX IF NOT EXISTS SPRING_AI_CHAT_MEMORY_CONVERSATION_ID_TIMESTAMP_IDX
ON SPRING_AI_CHAT_MEMORY(conversation_id, `timestamp`);
```

**表结构解析**：

- `conversation_id`：对话会话标识，支持多会话隔离
- `content`：消息内容
- `type`：消息类型（用户、助手、系统、工具）
- `timestamp`：时间戳，用于消息排序
- 复合索引：优化按会话 ID 和时间的查询性能

### Step 2: 实现 ChatMemoryService

```java
@Service
public class ChatMemoryService {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private JdbcChatMemoryRepository chatMemoryRepository;

    private ChatMemory chatMemory;

    @PostConstruct
    public void init() {
        this.chatMemory = MessageWindowChatMemory.builder()
                .chatMemoryRepository(chatMemoryRepository)
                .maxMessages(20)  // 限制消息窗口大小
                .build();
    }

    public String call(String message, String conversationId) {
        // 1. 创建用户消息
        UserMessage userMessage = new UserMessage(message);
        
        // 2. 存储用户消息到 memory
        this.chatMemory.add(conversationId, userMessage);
        
        // 3. 从 memory 获取对话历史
        List<Message> messages = chatMemory.get(conversationId);
        
        // 4. 调用 ChatModel 生成响应
        ChatResponse response = chatModel.call(new Prompt(messages));
        
        // 5. 存储 AI 响应到 memory
        chatMemory.add(conversationId, response.getResult().getOutput());
        
        return response.getResult().getOutput().getText();
    }
}
```

**核心机制**：
- `MessageWindowChatMemory`：支持消息窗口限制的内存实现
- `maxMessages`：控制保留的最大消息数量，避免 token 超限
- 手动管理消息的存储和检索流程

### Step 3: 配置数据源

```properties
# application.properties
spring.application.name=spring-ai-chat-memory-jdbc
server.port=8083

# JDBC Memory Repository 配置
spring.ai.chat.memory.repository.jdbc.initialize-schema=always
spring.ai.chat.memory.repository.jdbc.schema=classpath:schema-@@platform@@.sql
spring.ai.chat.memory.repository.jdbc.platform=mysql

# MySQL 数据源配置
spring.datasource.url=jdbc:mysql://localhost:3306/spring_ai_chat_memory?useUnicode=true&characterEncoding=UTF-8&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=${spring.datasource.password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
```

### JDBC Memory 优缺点

**优点**：
- 数据持久化，支持服务重启
- 支持多实例间共享对话历史
- 可扩展性强，支持大规模应用
- 支持复杂查询和数据分析

**缺点**：
- 配置相对复杂
- 存在网络延迟
- 需要维护数据库

## 运行效果演示

### Local Memory 运行日志

```
第一轮对话:
用户: hello, my name is glmapper
AI: Hello glmapper! Nice to meet you. How can I help you today?

第二轮对话:
用户: do you remember my name?
AI: Yes, I remember! Your name is glmapper. Is there anything specific you'd like to discuss?
```

### JDBC Memory 数据库记录

```sql
SELECT * FROM SPRING_AI_CHAT_MEMORY WHERE conversation_id = 'test-naming-202505281800';

| conversation_id           | content                    | type      | timestamp           |
|--------------------------|----------------------------|-----------|--------------------|
| test-naming-202505281800 | hello, my name is glmapper | USER      | 2025-01-20 10:30:15 |
| test-naming-202505281800 | Hello glmapper! Nice to... | ASSISTANT | 2025-01-20 10:30:16 |
| test-naming-202505281800 | do you remember my name?   | USER      | 2025-01-20 10:31:20 |
| test-naming-202505281800 | Yes, I remember! Your...  | ASSISTANT | 2025-01-20 10:31:21 |
```

## 实战测试验证

### 测试对话连续性

```java
@Test
@DisplayName("测试聊天记忆功能 - 上下文保持")
void testChatMemoryContextRetention() {
    String CONVERSATION_ID = "test-naming-202505281800";
    
    // 第一轮对话：自我介绍
    String firstMessage = "hello, my name is glmapper";
    String firstResponse = chatMemoryService.call(firstMessage, CONVERSATION_ID);
    
    // 第二轮对话：询问之前提到的信息
    String secondMessage = "do you remember my name?";
    String secondResponse = chatMemoryService.call(secondMessage, CONVERSATION_ID);
    
    // 验证AI是否记住了用户的名字
    assertTrue(secondResponse.contains("glmapper"), "AI 应该记住用户的名字");
}
```

### 测试对话隔离性

```java
@Test
@DisplayName("测试对话ID的非一致性")
void testConversationIdNonConsistency() {
    String CONVERSATION_ID1 = "test-naming-202505281801";
    String CONVERSATION_ID2 = "test-naming-202505281802";
    
    String message1 = "请记住这个数字：12345";
    String message2 = "刚才我说的数字是什么？";
    
    String response1 = chatMemoryService.call(message1, CONVERSATION_ID1);
    String response2 = chatMemoryService.call(message2, CONVERSATION_ID2);
    
    // 验证不同对话ID间的隔离性
    assertFalse(response2.contains("12345"), "不同对话ID应该相互隔离");
}
```

## 方案对比与选择建议

| 特性 | Local Memory | JDBC Memory |
|------|-------------|-------------|
| 数据持久化 | ❌ | ✅ |
| 配置复杂度 | 低 | 中 |
| 性能 | 高 | 中 |
| 多实例共享 | ❌ | ✅ |
| 扩展性 | 低 | 高 |
| 适用场景 | 开发/测试 | 生产环境 |

**选择建议**：
- **开发/测试阶段**：使用 Local Memory，快速验证功能
- **生产环境**：使用 JDBC Memory，确保数据可靠性
- **高并发场景**：考虑使用 Redis 等缓存方案
- **企业级应用**：JDBC + 数据库集群方案

## 常见问题与解决方案

### Q1: 为什么 AI 记不住之前的对话？
**A**: 检查对话 ID 是否一致，确保在同一会话中使用相同的 `conversationId`。

### Q2: JDBC Memory 初始化失败？
**A**: 确认数据库连接正常，检查 `spring.ai.chat.memory.repository.jdbc.initialize-schema=always` 配置。

### Q3: 对话历史过长导致 Token 超限？
**A**: 设置合适的 `maxMessages` 参数限制消息窗口大小。

```java
this.chatMemory = MessageWindowChatMemory.builder()
    .chatMemoryRepository(chatMemoryRepository)
    .maxMessages(10)  // 根据模型 token 限制调整
    .build();
```

## 最佳实践

1. **会话 ID 管理**：使用 UUID 或有意义的业务标识，建议格式：`user_{userId}_{timestamp}`
2. **消息窗口控制**：根据模型 token 限制合理设置 `maxMessages`（通常 10-20 条）
3. **异常处理**：实现 memory 操作的容错机制，避免单点故障
4. **性能优化**：使用数据库连接池，为高频查询字段建立索引
5. **数据清理**：定期清理过期对话数据，避免数据库膨胀
6. **监控告警**：监控 memory 操作的延迟和错误率

## 总结

Spring AI Chat Memory 提供了灵活的对话记忆管理能力，通过 Local 和 JDBC 两种存储方案，可以满足从开发测试到生产部署的不同需求。Local Memory 适合快速原型开发，而 JDBC Memory 则适合需要数据持久化的生产环境。

理解其核心机制和实现细节，有助于开发者根据实际场景选择合适的方案，构建出高质量的智能对话应用。

---

**项目地址**: [spring-ai-summary](https://github.com/glmapper/spring-ai-summary)

**相关文档**: 
- [Spring AI Documentation](https://docs.spring.io/spring-ai/reference/)
- [ChatMemory API Reference](https://docs.spring.io/spring-ai/reference/api/chat/chat-memory.html)