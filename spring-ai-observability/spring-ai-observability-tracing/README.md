# Spring AI Observability Tracing

该项目是基于 [Spring AI](https://docs.spring.io/spring-ai/) 构建的可观察性追踪示例，集成了 **OpenAI 客户端**（兼容阿里云 DashScope）、**工具调用（Function/Method）**、**向量存储（Redis VectorStore）** 和 **分布式追踪（Zipkin）** 功能。


通过本项目可以快速了解如何在 Spring Boot 应用中实现对 AI 模型调用的监控、追踪和可观测性管理。

---

## 📦 项目结构

```
spring-ai-observability-tracing
├── src/
│   ├── main/
│   │   ├── java/com/glmapper/ai/observability/tracing/
│   │   │   ├── controller/       # 各类 REST 接口（Chat, Image, Embedding, Tools）
│   │   │   ├── tools/            # 工具函数（天气查询、时间获取）
│   │   │   ├── storage/          # Redis VectorStore 存储封装
│   │   │   └── configs/          # 配置类
│   │   └── resources/
│   │       └── application.yml   # 配置文件
│   └── ...
├── docker-compose.yaml           # Zipkin 服务定义
├── pom.xml                       # Maven 项目配置
└── README.md                     # 当前文档
```

---

## 🧩 核心功能

| 模块 | 功能描述 |
|------|----------|
| ChatController | 调用 OpenAI 兼容接口进行聊天对话 |
| ImageController | 调用图像生成 API（如 Stable Diffusion） |
| EmbeddingController | 使用文本嵌入模型生成向量表示 |
| ToolCallingController | 支持 Function 和 Method 类型工具调用（天气、当前时间） |
| VectorStoreController | 使用 Redis 作为向量数据库进行文档存储与搜索 |
| Zipkin 集成 | 所有请求链路信息上报至 Zipkin，支持全链路追踪 |

---

## 🛠️ 技术栈

- Spring Boot 3.x
- Spring AI 1.0.x
- Redis VectorStore
- OpenAI Client（兼容 DashScope）
- Micrometer Tracing + Brave
- Zipkin 分布式追踪

---

## 🚀 快速启动

### 1. 启动 Zipkin（使用 Docker）

```bash
docker-compose up -d
```

访问：[http://localhost:9411](http://localhost:9411)

### 2. 修改配置

在 [application.yml](src/main/resources) 中更新以下参数：

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key-here
```

### 3. 启动应用

```bash
mvn spring-boot:run
```

或构建后运行：

```bash
mvn clean package
java -jar target/spring-ai-observability-tracing-*.jar
```

---

## 🌐 接口列表

| 接口路径                                  | 描述                     |
|---------------------------------------|------------------------|
| /observability/chat?message=xxx       | 发送聊天消息给 AI 模型          |
| /observability/image                  | 生成一张图片                 |
| /observability/embedding              | 获取 "hello world" 的文本嵌入 |
| /observability/embedding/generic      | 使用指定模型获取嵌入             |
| /observability/tools/function         | 调用 Function 工具（天气）     |
| /observability/tools/method           | 调用 Method 工具（当前时间）     |
| /observability/vector/store?text=xxx  | 存储文档到 Redis 向量库        |
| /observability/vector/search?text=xxx | 在向量库中搜索相似内容            |
| /observability/vector/delete?id=xxx   | 删除向量库中指定id内容                 |

---

## 📊 监控指标

- Prometheus 指标地址：\`http://localhost:8088/actuator/prometheus\`
- 健康检查：\`http://localhost:8088/actuator/health\`
- 所有链路数据会自动上报至 Zipkin

---

## 📝 注意事项

- 确保已安装并运行 Redis。
- 如需部署到生产环境，请关闭 debug 日志并调整采样率（`management.tracing.sampling.probability`）。
- 若更换为其他 OpenAI 兼容平台，请修改对应的 `base-url` 和 `api-key`。
