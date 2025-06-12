## spring-ai-vector-milvus

该工程模块主要是集成 Milvus 的向量存储功能，提供了一个使用 Milvus 存储向量并执行相似性搜索的简单示例。

### 1、Milvus 数据库部署与初始化

#### Milvus 安装

Milvus 是一个开源的向量数据库，用于存储和检索高维向量数据。本项目是使用 Docker 来运行 Milvus，当然你也可以选择其他方式安装 Milvus或者使用已经部署好的 Milvus 服务。

> PS: 如果你不运行 spring-ai-rag 模块和 spring-ai-embedding 模块，可以跳过此步骤。

这个项目使用的 milvus 版本是 2.5.0 版本，安装方式见：[Install Milvus in Docker](https://milvus.io/docs/install_standalone-docker.md)。

> ⚠️本人的电脑是 Mac Air M2 芯片，使用官方文档中的 docker-compose 文件启动 Milvus 时，遇到 `milvus-standalone` 镜像不匹配问题。

#### 创建 Collection（向量集合）

> 注意：embedding 维度需与模型一致，否则会报错。

* 创建 Collection 的 curl 示例
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
* 加载集合（load collection）

```bash
curl -X POST "http://localhost:19530/v2/vectordb/collections/load" \
  -H "Content-Type: application/json" \
  -d '{
    "collectionName": "vector_store"
  }'
```

* 删除 Collection

```bash
POST /v2/vectordb/collections/drop HTTP/1.1
Authorization: Bearer root:Milvus
Content-Length: 38
Content-Type: application/json
Host: localhost:19530
User-Agent: HTTPie
{
  "collectionName": "vector_store"
}
```

### 2、引入依赖和配置

* 依赖

```xml
 <dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-starter-vector-store-milvus</artifactId>
</dependency>
```

* 配置

```properties
# embedding model
# 这里替换成你自己的 api-key
spring.ai.openai.api-key=${spring.ai.openai.api-key}
spring.ai.openai.embedding.base-url=https://ark.cn-beijing.volces.com/api/v3
spring.ai.openai.embedding.embeddings-path=/embeddings
spring.ai.openai.embedding.options.model=ep-20250506170049-dzjj7

spring.ai.vectorstore.milvus.client.host=localhost
spring.ai.vectorstore.milvus.client.port=19530
#spring.ai.vectorstore.milvus.client.username=root
#spring.ai.vectorstore.milvus.client.password=Milvus
#spring.ai.vectorstore.milvus.databaseName="default"
spring.ai.vectorstore.milvus.collection.name=vector_store
```

#### 文档数据初始化与嵌入

按照示例，你可以将你需要存储的文件放在 `src/main/resources/files` 目录下，然后使用 `LangChainTextSplitter` 类来读取文件内容，切分为小块，并将其嵌入到 Milvus 向量库中。

核心代码如下：

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

* controller 代码如下
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

* ChatClient 配置（Advisor API 自动拼接）
```java
@Bean
public ChatClient chatClient(OpenAiChatModel chatModel, VectorStore vectorStore) {
    return ChatClient.builder(chatModel)
        .defaultAdvisors(new QuestionAnswerAdvisor(vectorStore))
        .defaultSystem("You are a friendly chat bot that answers question with json always")
        .build();
}
```

#### 效果验证

1. 启动 spring-ai-rag 服务（确保 Milvus 已启动并初始化好集合）
2. 先访问 `/api/qwen/embedding_test` 完成文档嵌入
3. 再访问 `/api/qwen/chat?userInput=你的问题`，可检索并返回文档相关内容