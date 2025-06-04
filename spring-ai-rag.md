最近我在整理 spring ai  的一些技术点，我们都知道，spring ai 官方文档中提供的代码片段只是作为参考，大多数情况因版本变化导致的文档滞后问题也和大多数开源项目一样，如果我们想 step by step 通过官方文档来运行一个完整的案例，是非常困难的，即使是那种非常入门的 demo；目前技术社区关于基于 spring ai 构建 rag 的案例还比较少，极少数的还是非 GA 版本的案例，已经和当前版本相差较远，所以写一篇关于这方面的文章来帮助大家入门 spring ai rag。

# Spring AI RAG 实践：从 0 到 1 构建检索增强生成系统

最近我在整理 spring ai 的一些技术点，我们都知道，spring ai 官方文档中提供的代码片段只是作为参考，大多数情况因版本变化导致的文档滞后问题也和大多数开源项目一样，如果我们想 step by step 通过官方文档来运行一个完整的案例，是非常困难的，即使是那种非常入门的 demo；目前技术社区关于基于 spring ai 构建 rag 的案例还比较少，极少数的还是非 GA 版本的案例，已经和当前版本相差较远，所以写一篇关于这方面的文章来帮助大家入门 spring ai rag。

## 一、RAG 基本流程与架构

### 1.1 RAG 工作流程

1. **检索**：根据用户问题，从向量数据库中检索相关文档。
2. **增强**：将检索到的上下文内容注入到 LLM 的提示词中。
3. **生成**：LLM 基于用户问题和上下文生成最终答案。

### 1.2 Spring AI RAG 典型架构

- **Vector Store**: 支持 Milvus、ChromaDB 等主流向量数据库。
- **Embedding Model**: 支持 OpenAI、阿里云通义千问、豆包等主流嵌入模型。
- **Prompt Orchestration**: 灵活的系统提示词模板和用户消息拼接。

![img](https://vspicgo.oss-cn-shanghai.aliyuncs.com/typora/1*CuVO0YrCKTOySKvyY0EBxw.png)


## 二、环境准备与依赖配置

### 2.1 环境要求
- JDK 21+
- Maven 3.6+
- Docker（用于运行 Milvus 向量数据库）
- 网络连接（访问 LLM API）

### 2.2 依赖配置（pom.xml 片段）
```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-advisors-vector-store</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-model-openai</artifactId>
</dependency>
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
</dependency>
<!-- 提供文本切分使用 -->
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-core</artifactId>
    <version>1.0.1</version>
</dependency>
```

### 2.3 application.properties 关键配置
```properties
# chat model config
spring.ai.openai.chat.api-key=xxx
spring.ai.openai.chat.base-url=https://dashscope.aliyuncs.com/compatible-mode
spring.ai.openai.chat.options.model=qwen-plus
# embedding model config
spring.ai.openai.embedding.api-key=xxx
spring.ai.openai.embedding.base-url=https://ark.cn-beijing.volces.com/api/v3
spring.ai.openai.embedding.options.model=ep-20250506170049-dzjj7
# milvus config
spring.ai.vectorstore.milvus.client.host=localhost
spring.ai.vectorstore.milvus.client.port=19530
spring.ai.vectorstore.milvus.collection.name=vector_store
```

## 三、Milvus 数据库部署与初始化

### 3.1 安装 Milvus
推荐参考官方文档：[在 Docker 中运行 Milvus (Linux)](https://milvus.io/docs/zh/install_standalone-docker.md)

### 3.2 创建 Collection（向量集合）

> 注意：embedding 维度需与模型一致，否则会报错。

#### 创建 Collection 的 curl 示例：
```bash
curl -X POST "http://localhost:19530/v2/vectordb/collections/create" \
  -H "Authorization: Bearer root:Milvus" \
  -H "Content-Type: application/json" \
  -d '{
    "collectionName": "vector_store",
    "schema": {
      "fields": [
        { "fieldName": "embedding", "dataType": "FloatVector", "elementTypeParams": { "dim": "2048" } },
        { "fieldName": "content", "dataType": "VarChar", "elementTypeParams": { "max_length": 512000 } },
        { "fieldName": "metadata", "dataType": "JSON" },
        { "fieldName": "doc_id", "dataType": "VarChar", "isPrimary": true, "elementTypeParams": { "max_length": 512 } }
      ]
    },
    "indexParams": [
      { "fieldName": "embedding", "metricType": "COSINE", "indexName": "embedding_index", "indexType": "AUTOINDEX" },
      { "fieldName": "doc_id", "indexName": "doc_id_index", "indexType": "AUTOINDEX" }
    ]
  }'
```

### 3.3 加载集合（load collection）

#### 加载 Collection 的 curl 示例：
```bash
curl -X POST "http://localhost:19530/v2/vectordb/collections/load" \
  -H "Content-Type: application/json" \
  -d '{
    "collectionName": "vector_store"
  }'
```


## 四、文档数据初始化与嵌入

### 4.1 示例文档准备
将待检索的知识文档（如 LLM-infer.md）放入 `src/main/resources/files/` 目录。

### 4.2 嵌入与写入向量库（核心代码）
```java
// LangChainTextSplitter.java
/**
 * 读取本地 markdown 文档，切分为小块后写入 Milvus 向量库
 */
public void embedding() {
    try {
        // 1. 创建文本切分器
        TextSplitter splitter = new TokenTextSplitter();
        // 2. 读取本地 markdown 文件内容
        URL path = LangChainTextSplitter.class.getClassLoader().getResource("classpath:files/LLM-infer.md");
        String mdContent = Files.readString(Paths.get(path.toURI()), StandardCharsets.UTF_8);
        // 3. 构造 Document 对象
        Document doc = new Document(mdContent);
        // 4. 切分为小块
        List<Document> docs = splitter.split(doc);
        // 5. 写入向量库
        this.vectorStore.add(docs);
    } catch (Exception e) {
       // 异常信息
    }
}
```

## 五、核心逻辑代码详解

### 5.1 ChatController 主要接口
```java
// 初始化嵌入数据
/**
 * 触发文档嵌入，将本地文档内容写入向量库
 */
@GetMapping("embedding_test")
public String embedding() {
    langChainTextSplitter.embedding();
    return "Embedding completed successfully.";
}

// RAG 聊天接口
/**
 * 基于用户输入，检索相关文档并拼接到系统提示词，实现 RAG 问答
 * @param userInput 用户输入
 * @return LLM 生成的答案
 */
@GetMapping("/chat")
public String prompt(@RequestParam String userInput) {
    // 1. 构造用户消息
    Message userMessage = new UserMessage(userInput);
    // 2. 检索相似文档
    List<Document> similarDocuments = vectorStore.similaritySearch(userInput);
    // 3. 拼接检索到的内容
    String tncString = similarDocuments.stream().map(Document::getFormattedContent).collect(Collectors.joining("\n"));
    // 4. 构造系统提示词
    Message systemMessage = new SystemPromptTemplate("You are a helpful assistant. Here are some relevant documents:\n\n {documents}")
        .createMessage(Map.of("documents", tncString));
    // 5. 构造 Prompt
    Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
    // 6. 调用 LLM 生成答案
    return chatClient.prompt(prompt).call().content();
}
```

### 5.2 ChatClient 配置（Advisor API 自动拼接）
```java
@Bean
public ChatClient chatClient(OpenAiChatModel chatModel, VectorStore vectorStore) {
    return ChatClient.builder(chatModel)
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
        .defaultSystem("You are a friendly chat bot that answers question with json always")
        .build();
}
```

## 六、效果验证

1. 启动 spring-ai-rag 服务（确保 Milvus 已启动并初始化好集合）
2. 先访问 `/api/qwen/embedding_test` 完成文档嵌入
3. 再访问 `/api/qwen/chat?userInput=你的问题`，可检索并返回文档相关内容


## 八、总结

Spring AI RAG 方案为 Java 开发者提供了现代化的检索增强生成能力，结合 Milvus 等向量数据库和主流大模型，可以快速搭建企业级知识问答、智能客服等应用。建议结合自身业务场景，灵活选型和优化。
