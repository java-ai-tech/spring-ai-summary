# Spring AI Observability Metric

该项目是基于 [Spring AI](https://docs.spring.io/spring-ai/) 构建的可观察性追踪示例，集成了 **OpenAI 客户端**（兼容阿里云 DashScope）、**工具调用（Function/Method）** 和 **向量存储（Redis VectorStore）** 功能。
 

通过本项目可以快速了解如何在 Spring Boot 应用中实现对 AI 模型调用的监控、追踪和可观测性管理。


## 模块结构

```
spring-ai-observability-metric
├── src/
│   ├── main/
│   │   ├── java/com/glmapper/ai/observability/tracing/
│   │   │   ├── controller/       # 各类 REST 接口（Chat, Image, Embedding, Tools）
│   │   │   ├── tools/            # 工具函数（天气查询、时间获取）
│   │   │   ├── storage/          # Redis VectorStore 存储封装
│   │   │   └── configs/          # 配置类
│   │   └── resources/
│   │       └── application.yml   # 配置文件
├── pom.xml                       # Maven 项目配置
└── README.md                     # 当前文档
```

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
### 1. 修改配置

在 [application.yml](src/main/resources) 中更新以下参数：

```yaml
spring:
  ai:
    openai:
      api-key: your-api-key-here
```

### 2. 启动应用

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

## 📊观测指标查看

项目使用 Micrometer 和 Spring Boot Actuator 来暴露和管理指标数据。可以通过以下步骤查看观测指标：

1. **启动应用**：确保应用已经成功启动。

2. **访问指标端点**：通过浏览器或 HTTP 客户端访问 `/actuator/metrics` 端点。

   ```bash
   curl http://localhost:8087/actuator/metrics
   ```

3. **查看具体指标**：可以通过指定指标名称来查看具体的指标详情。

   ```bash
   curl http://localhost:8087/actuator/metrics/[metricName]
   ```

   其中 `[metricName]` 是你想查看的具体指标名称。

4. **Prometheus 格式**：如果需要以 Prometheus 格式查看指标，可以访问 `/actuator/prometheus` 端点。

   ```bash
   curl http://localhost:8087/actuator/prometheus
   ```

这些指标包括但不限于 JVM 指标、系统指标、HTTP 请求统计等。通过这些指标，可以更好地监控和诊断应用程序的运行状态。

## 📝 注意事项

- 确保已安装并运行 Redis。
- 如需部署到生产环境，请关闭 debug 日志并调整采样率（`management.tracing.sampling.probability`）。
- 若更换为其他 OpenAI 兼容平台，请修改对应的 `base-url` 和 `api-key`。
