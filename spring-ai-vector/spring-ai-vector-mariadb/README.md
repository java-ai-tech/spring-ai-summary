## spring-ai-vector-mariadb 

该工程模块主要是集成 mariadb 的向量存储功能，提供了一个使用 mariadb 存储向量并执行相似性搜索的简单示例。

[mariadb使用说明](https://mariadb.org/documentation/) 

- 关系数据库简介
-   10分钟MariaDB服务器入门
-   SQL 语句列表
-   有用的 MariaDB 服务器查询
-  MariaDB 服务器文档

## 前提条件

### 1、mariadb 数据库部署与初始化

mariadb 是一个开源的向量数据库，用于存储和检索高维向量数据。本项目是使用 Docker 来运行 mariadb，当然你也可以选择其他方式安装 mariadb或者使用已经部署好的 mariadb 服务。
[mariadb镜像地址](https://hub.docker.com/_/mariadb)
```

② 一个 `EmbeddingModel` 实例，用于计算文档嵌入。有多个[选项](https://docs.spring.io/spring-ai/reference/api/embeddings.html#available-implementations)可供选择

③ 一个 API 密钥，给 EmbeddingModel 用于生成向量数据


## 自动配置
Spring AI 为 Mariadb 向量数据库提供了 Spring Boot 自动配置。要启用它~~，请将以下依赖添加到项目的 Maven pom.xml 文件中：~~
```
```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-vector-store-mariadb</artifactId>
    </dependency>
</dependencies>
````

## 配置文件

在你启动项目之前，你需要修改 `application.yml` 文件。

```yaml
server:
  port: 8080

spring:
  datasource:
    url: ${BASE_HOST}
    username:  ${BASE_NAME}
    password:  ${BASE_PWD}
    driver-class-name: org.mariadb.jdbc.Driver
  ai:
    vectorstore:
      mariadb:
        # 启用模式初始化
        initialize-schema: true
        # 设置距离计算类型为余弦相似度
        distance-type: COSINE
        # 定义向量维度为1536
        dimensions: 1536
    openai:
      api-key: ${API_KEY}
      embedding:
        base-url: https://dashscope.aliyuncs.com/compatible-mode/v1
        embeddings-path: /embeddings
        options:
          model: text-embedding-v4

```
修改完成之后即可以在 IDEA 中启动单元测试。



