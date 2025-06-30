# Spring AI Nacos MCP Server

这是一个Spring AI Nacos MCP服务端模块，提供基于Nacos注册中心的MCP（Model Context Protocol）服务实现。

## 功能特性
- 基于Nacos的服务注册与发现
- 提供天气查询工具服务
- 支持异步消息通信
- 完整的MCP协议支持

## 服务配置
配置在`application.yml`中设置：
```yaml
spring:
  ai:
    mcp:
      server:
        name: weather-mcp-server   # MCP服务名称（需全局唯一）
        version: 1.0.0            # 服务版本（语义化版本号）
        type: ASYNC               # 通信类型（ASYNC适用于响应式应用）
        sse-message-endpoint: /mcp/messages  # SSE消息端点路径
        capabilities:
          tool: true             # 启用工具调用能力
          resource: false          # 禁用资源访问能力
          prompt: false            # 禁用提示词功能
          completion: false        # 禁用补全功能
    alibaba:
      mcp:
        nacos:
          namespace: 07fa822a-269c-4b42-a4ab-d243351e57aa  # Nacos命名空间ID
          server-addr: 127.0.0.1:8848  # Nacos服务器地址
          username: nacos         # Nacos认证用户名
          password: nacos         # Nacos认证密码
          registry:
            enabled: true       # 启用服务注册
            service-group: mcp-server  # 服务分组
            service-name: weather-mcp-server  # 服务名称（应与server.name一致）
```

## Nacos初始化配置
在启动服务前，请确保已正确初始化Nacos：
1. 启动Nacos服务器（可使用Docker Compose）
```bash
docker-compose up -d
```

2. 创建命名空间（如未使用默认public空间）
   - 登录Nacos控制台（http://localhost:8080）
   - 在"命名空间"页面创建新命名空间
   - 使用对应的namespace ID配置

3. 配置服务元数据（可选）
   - 可通过Nacos控制台设置服务的元数据信息
   - 这些信息可用于服务发现时的过滤条件

## 启动服务
确保已启动Nacos服务器，然后运行：
```bash
mvn spring-boot:run
```

## 主要组件
- `NacoseServerApplication` - 应用主类
- `WeatherService` - 天气查询工具服务