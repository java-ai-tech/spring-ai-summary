# Lab: Spring AI 构建 RAG

不得不吐槽下，Spring AI 的文档实在是太差了，很多地方都没有说明白，想要 step by step 的去完成一个 RAG 的应用，实在是太难了。

## 安装 milvus

我参考的是这个官方文档，[在 Docker 中运行 Milvus (Linux)](https://milvus.io/docs/zh/install_standalone-docker.md) ；为什么？因为
docker-compose 的方式安装 Milvus
时，standalone 拉到的镜像有问题。（ps：我的是 mac air）

## 初始化 milvus 数据库

* 创建 Collection

我这里使用的 embedding 是豆包的，维度是 2048（维度对不上会有问题），必须创建索引，否则在使用客户端查询是会报
`index not found[collection=vector_store]` 类似的错误。

```bash
POST /v2/vectordb/collections/create HTTP/1.1
Authorization: Bearer root:Milvus
Content-Length: 908
Content-Type: application/json
Host: localhost:19530
User-Agent: HTTPie

{
  "collectionName": "vector_store",
  "schema": {
    "fields": [
      {
        "fieldName": "embedding",
        "dataType": "FloatVector",
        "elementTypeParams": {
          "dim": "2048"
        }
      },
      {
        "fieldName": "content",
        "dataType": "VarChar",
        "elementTypeParams": {
          "max_length": 512000
        }
      },
      {
        "fieldName": "metadata",
        "dataType": "JSON"
      },
      {
        "fieldName": "doc_id",
        "dataType": "VarChar",
        "isPrimary": true,
        "elementTypeParams": {
          "max_length": 512
        }
      }
    ]
  },
  "indexParams": [
    {
      "fieldName": "embedding",
      "metricType": "COSINE",
      "indexName": "embedding_index",
      "indexType": "AUTOINDEX"
    },
    {
      "fieldName": "doc_id",
      "indexName": "doc_id_index",
      "indexType": "AUTOINDEX"
    }
  ]
}
```

* 删除 Collection

如果你需要删除 Collection，可以使用以下命令：

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

* load 集合
  在查询之前，需要先加载集合，否则会报错 `collection not loaded[collection=vector_store]`。当然也可以在代码中执行 load 动作

```bash
POST /v2/vectordb/collections/load HTTP/1.1
Content-Length: 38
Content-Type: application/json
Host: localhost:19530
User-Agent: HTTPie

{
  "collectionName": "vector_store"
}
```

## 运行 spring-ai-rag
在运行之前，确保你已经安装了 Milvus，并且已经创建了 `vector_store` 集合。
在项目根目录下，执行以下命令：

```bash
./mvnw spring-boot:run
```
如果你使用的是 IntelliJ IDEA，可以直接运行 `SpringAiRagApplication` 类。
启动之后先执行 embedding 接口，将