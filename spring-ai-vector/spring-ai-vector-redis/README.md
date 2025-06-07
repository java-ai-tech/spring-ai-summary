# spring-ai-vector-redis 

该工程模块主要是集成 Redis 的向量存储功能，提供了一个使用 Redis 存储向量并执行相似性搜索的简单示例。

[Redis Search and Query](https://redis.io/docs/interact/search-and-query/) 扩展了 Redis OSS 的核心功能，使你可以将 Redis 用作向量数据库：

- 在哈希或 JSON 文档中存储向量及其关联的元数据
- 检索向量
- 执行向量搜索

## 前提条件

① 一个 Redis Stack 实例，这里使用 [Docker](https://hub.docker.com/r/redis/redis-stack) 镜像 redis/redis-stack:latest，你可以直接使用下面的命令本地启动 Redis

```bash
docker run -d \
--name redis-stack \
-p 6379:6379 -p 8001:8001 \
-v /your_path/data:/data \
-e REDIS_ARGS="--requirepass 123456" \
redis/redis-stack:latest
```

② 一个 `EmbeddingModel` 实例，用于计算文档嵌入。有多个[选项](https://docs.spring.io/spring-ai/reference/api/embeddings.html#available-implementations)可供选择

③ 一个 API 密钥，给 EmbeddingModel 用于生成向量数据





这里使用的是 Redis 来存储向量数据的， 对应的依赖是 `spring-ai-starter-vector-store-redis`，如下：
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-redis</artifactId>
    </dependency>
</dependencies>
```



### 配置文件

在你启动项目之前，你需要修改 `application.yml` 文件。

```yaml
server:
  port: 8080

spring:
  application:
    name: redis-vector-store
  data:
    redis:
      host: ${REDIS_HOST:localhost}
      port: ${REDIS_PORT:6379}
      username: ${REDIS_USERNAME:}
      password: ${REDIS_PASSWORD:123456}
  ai:
    openai:
      api-key: ${YOUR_QWEN_API_KEY}
      embedding:
        base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
        embeddings-path: /embeddings
        options:
          model: text-embedding-v4
    vectorstore:
      redis:
      	# 是否初始化所需的索引结构
        initialize-schema: true
        # 用于存储向量的索引名称
        index-name: glmapper
        # Redis 键的前缀
        prefix: glmapper_
```
修改完成之后即可以在 IDEA 中启动单元测试。



