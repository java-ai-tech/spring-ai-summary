# spring-ai-chat-multi

这里主要是验证接入不同 chat model 的场景，本工程模块中主要以 openai 模型和 deepseek model 为例。

```java
@Configuration
@ConditionalOnProperty(name = "spring.ai.chat.client.enabled", havingValue = "false")
public class MultiChatClientConfigs {

    @Bean
    public ChatClient openAiChatClient(OpenAiChatModel chatModel) {
        return ChatClient.create(chatModel);
    }

    @Bean
    public ChatClient deepSeekChatClient(DeepSeekChatModel chatModel) {
        return ChatClient.builder(chatModel)
                .defaultSystem("You are a friendly chat bot that answers question with json always")
                .build();
    }
}
```

需要关注 `spring.ai.chat.client.enabled` 配置，这里默认关闭自动配置，使用手动配置的方式来接入不同的 chat model。

### 修改配置文件
在你启动项目之前，你需要修改 `application.properties` 文件，添加 OpenAI 和 DeepSeek 的 API 密钥；这里的 OpenAI 协议模型实际上是火山方舟的 Ark 模型，我们
这里是以 OpenAI 协议方式接进来的。

```properties
# use openai protocol model
spring.ai.openai.api-key=${spring.ai.openai.api-key}
spring.ai.openai.chat.base-url=https://ark.cn-beijing.volces.com/api/v3
spring.ai.openai.chat.completions-path=/chat/completions
spring.ai.openai.chat.options.model=ep-20250117161524-4knd5

# use deepseek model
spring.ai.deepseek.api-key=${spring.ai.deepseek.api-key}
spring.ai.deepseek.base-url=https://api.deepseek.com
spring.ai.deepseek.chat.completions-path=/v1/chat/completions
spring.ai.deepseek.chat.options.model=deepseek-chat
```

修改完配置之后即可启动运行项目，然后根据 controller 中提供的接口进行访问测试。
```bash
http://localhost:8083/api/multi-chat/chat
```