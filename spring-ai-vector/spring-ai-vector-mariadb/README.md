# `spring-ai-vector-mariadb` 文档

## 1. 概述

本模块 `spring-ai-vector-mariadb` 提供了一个基于 **MariaDB** 的向量存储实现，支持将高维向量数据存入 MariaDB 数据库，并能够执行相似性搜索（如余弦相似度）。适用于需要结合关系型数据库和 AI 向量能力的场景。

该项目集成了 [Spring AI](https://docs.spring.io/spring-ai/reference/html/) 和 [MariaDB](https://mariadb.org/)，为开发者提供了一个快速上手的向量存储解决方案。

---

## 2. 功能特性

- 使用 MariaDB 存储文档及其向量表示
- 支持通过 Spring Boot 自动配置向量数据库连接
- 提供简单的 CRUD 操作（增删查）
- 支持基于余弦相似度的向量检索
- 集成 OpenAI 兼容的嵌入模型接口（如阿里云 DashScope）

---

## 3. 技术栈

| 技术 | 版本/说明                                       |
|------|---------------------------------------------|
| Java | JDK 21+                                     |
| Spring Boot | 最新稳定版本                                      |
| Spring AI | 集成 `spring-ai-starter-vector-store-mariadb` |
| MariaDB | 使用 JDBC 客户端 `mariadb-java-client:3.5.2`     |
| 嵌入模型 | 支持 OpenAI 接口兼容模型，如阿里云 DashScope             |

---

## 4. 项目结构

```
spring-ai-vector-mariadb/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.glmapper.ai.vector/
│   │   │       ├── MariadbVectorApplication.java     # 应用启动类
│   │   │       └── storage/
│   │   │           └── VectorStoreStorage.java       # 向量存储业务逻辑封装
│   │   └── resources/
│   │       └── application.yml                       # 主配置文件
│   └── test/
│       ├── java/
│       │   └── storage/
│       │       └── VectorStoreStorageTest.java       # 单元测试类
│       └── resources/
│           └── application-test.yml                  # 测试环境配置文件
├── README.md                                         # 项目说明文档（当前文件）
└── pom.xml                                           # Maven 构建配置
```


---

## 5. 环境依赖与准备

### 5.1 MariaDB 数据库部署

推荐使用 Docker 快速部署 MariaDB 实例：

```bash
docker run -d \
  --name mariadb \
  -e MYSQL_ROOT_PASSWORD=root \
  -p 3306:3306 \
  mariadb:latest
```


①  初始化数据库并创建用于向量存储的表结构（可由 Spring Boot 自动完成）。

② 一个 `EmbeddingModel` 实例，用于计算文档嵌入。有多个[选项](https://docs.spring.io/spring-ai/reference/api/embeddings.html#available-implementations)可供选择

③ 一个 API 密钥，给 EmbeddingModel 用于生成向量数据
> 注意：确保在 `application.yml` 中配置正确的数据库连接地址、用户名和密码。

---

## 6. 配置说明

### 6.1 核心配置 (`application.yml`)

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${BASE_HOST}  # 如 jdbc:mariadb://localhost:3308/vector_test
    username: ${BASE_NAME}
    password: ${BASE_PWD}
    driver-class-name: org.mariadb.jdbc.Driver

  ai:
    vectorstore:
      mariadb:
        initialize-schema: true     # 是否自动初始化表结构
        distance-type: COSINE       # 距离计算类型
        dimensions: 1536            # 向量维度
    openai:
      api-key: ${API_KEY}           # 嵌入模型 API Key
      embedding:
        base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
        embeddings-path: /embeddings
        options:
          model: text-embedding-v4  # 使用的嵌入模型
```


### 6.2 测试环境配置 (`application-test.yml`)

```yaml
spring:
  datasource:
    url: jdbc:mariadb://localhost:3308/vector_test
    username: root
    password: root
  ai:
    vectorstore:
      mariadb:
        initialize-schema: false   # 不再初始化 schema
```


---

## 7. 核心类说明

### 7.1 `MariadbVectorApplication.java`

主应用启动类，使用 `@SpringBootApplication` 注解启用 Spring Boot 自动配置。

### 7.2 `VectorStoreStorage.java`

封装了对向量存储的操作，包括：

- `store(List<Document>)`: 将文档及向量存入数据库
- `search(String)`: 执行相似性搜索，返回匹配结果
- `delete(Set<String>)`: 删除指定 ID 的向量记录

依赖注入 `VectorStore` 实现具体的向量操作逻辑。

---

## 8. 单元测试

### 8.1 `VectorStoreStorageTest.java`

单元测试验证向量存储与搜索功能是否正常。测试步骤如下：

1. 插入预定义文档
2. 执行相似性搜索
3. 验证结果非空
4. 清理数据（每个测试后删除插入的数据）

运行命令：

```bash
mvn test
```


---

## 9. 使用指南

### 9.1 启动应用

确保已正确配置数据库连接信息和 API 密钥后，直接运行：

```bash
mvn spring-boot:run
```


或在 IDE（如 IntelliJ IDEA）中运行 `MariadbVectorApplication` 类。

### 9.2 示例调用

你可以通过 REST API 或自定义服务调用 [VectorStoreStorage](spring-ai-vector/spring-ai-vector-mariadb/src/main/java/com/glmapper/ai/vector/storage/VectorStoreStorage.java) 方法进行文档存储与查询。

---

## 10. 依赖管理

### 10.1 Maven 依赖 ([pom.xml](spring-ai-vector/spring-ai-vector-mariadb/pom.xml))

```xml
<dependency>
  <groupId>org.springframework.ai</groupId>
  <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
</dependency>

<dependency>
  <groupId>org.mariadb.jdbc</groupId>
  <artifactId>mariadb-java-client</artifactId>
  <version>3.5.2</version>
</dependency>
```


---

## 11. 注意事项

- 确保 MariaDB 已正确安装并启动。
- 如果遇到 SQL 异常，请检查 `initialize-schema` 设置以及数据库权限。
- 向量维度需与使用的 Embedding 模型一致（默认为 1536）。
- 在生产环境中应关闭 `initialize-schema`，避免误删数据。

---

## 12. 参考资料

- [Spring AI 文档](https://docs.spring.io/spring-ai/reference/html/)
- [MariaDB 官方文档](https://mariadb.org/documentation/)
- [DashScope 开发者文档](https://help.aliyun.com/zh/dashscope/developer-reference/introduction)
- [Docker Hub - MariaDB](https://hub.docker.com/_/mariadb)

--- 

如有问题或建议，请联系维护人员。