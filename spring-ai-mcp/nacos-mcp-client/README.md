# Spring AI Nacos MCP Client

这是一个Spring AI Nacos MCP客户端模块，提供基于Nacos注册中心的MCP（Model Context Protocol）客户端实现。

## 功能特性
- 基于Nacos的服务发现
- 集成OpenAI兼容的聊天模型
- 支持与MCP服务端通信
- 提供REST API进行对话交互

## 客户端配置
配置在`application.yml`中设置：
```yaml
spring:
  ai:
    openai:
      api-key: sk-281ba3f2d6e24c539fac28c57725a9d6  # DashScope平台API密钥
      chat:
        base-url: https://dashscope.aliyuncs.com/compatible-mode  # 兼容模式基础URL
        completions-path: /v1/chat/completions  # 补全路径
        options:
          model: qwen-plus  # 使用的模型类型
    mcp:
      client:
        name: mcp-nacos-client  # 客户端名称（需全局唯一）
        version: 1.0.0         # 客户端版本（语义化版本号）
        request-timeout: 30s   # 请求超时时间
        type: ASYNC            # 通信类型（ASYNC适用于响应式应用）
        enabled: true          # 启用MCP客户端
    alibaba:
      mcp:
        nacos:
          namespace: 07fa822a-269c-4b42-a4ab-d243351e57aa  # Nacos命名空间ID
          server-addr: 127.0.0.1:8848  # Nacos服务器地址
          username: nacos      # Nacos认证用户名
          password: nacos      # Nacos认证密码
          client:
            enabled: true    # 启用Nacos客户端
            sse:
              connections:
                server1:
                  service-name: weather-mcp-server  # 要连接的服务名称
                  version: 1.0.0                    # 服务版本
```

## 启动客户端
确保已启动Nacos服务器和MCP服务端，然后运行：
```bash
mvn spring-boot:run
```

## 主要组件
- `NacoseClientApplication` - 应用主类
- `ChatController` - 对话交互的REST控制器
- `McpClientConfig` - MCP客户端配置类