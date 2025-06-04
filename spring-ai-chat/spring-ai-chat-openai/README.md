# spring-ai-chat-openai 

该工程模块主要是集成 OpenAI 模型的聊天功能，提供了一个简单的接口来与 OpenAI 进行对话。

这里使用的是 OpenAI 协议来接入模型的， 对应的依赖是 `spring-ai-starter-model-openai`，如下：

```xml
<dependencies>
    <dependency>
        <groupId>org.springframework.ai</groupId>
        <artifactId>spring-ai-starter-model-openai</artifactId>
    </dependency>
</dependencies>
```

### 修改配置文件

在你启动项目之前，你需要修改 `application.properties` 文件，添加 OpenAI 的 API 密钥。

```properties
# use openai protocol model
spring.ai.openai.api-key=${spring.ai.openai.api-key}
spring.ai.openai.chat.base-url=https://api.openai.com
spring.ai.openai.chat.completions-path=/v1/chat/completions
```
修改完成之后即可以在 IDEA 中启动项目，然后根据 controller 中提供的接口进行访问测试。

⚠️ **由于本人为开通 OpenAI 的 API 访问，因此这里一笔带过**