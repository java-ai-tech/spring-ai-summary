# spring-ai-chat-multi-openai

如果不同的 chat model 都是通过 OpenAI 协议接入的，那么可以参考 `spring-ai-chat-multi-openai` 模块的实现。
官方文档中提供案例实例 [Multiple OpenAI-Compatible API Endpoints](https://docs.spring.io/spring-ai/reference/api/chatclient.html#_multiple_openai_compatible_api_endpoints) 是有问题的，
主要是 1.0.0 版本中并没有提供 OpenAiApi 的自动配置类，因此我们需要手动配置。

```java

/**
* 这里配置一个默认的 OpenAI API 客户端；这里参考了官方文档，但是官方文档中是自动注入 baseOpenAiApi，实际上有问题
* 因为 OpenAiApi 并没有在自动配置中注册，所以需要手动创建一个 Bean。
*
* @return OpenAiApi
*/
@Bean
public OpenAiApi baseOpenAiApi() {
    return OpenAiApi.builder()
    .apiKey(System.getenv("DEEPSEEK_API_KEY"))
    .build();
}
```

### 修改配置文件

在你启动项目之前，需要在环境变量中配置 DOUBAO_API_KEY 和 DEEPSEEK_API_KEY，或者直接修改 `MultiChatClientService` 类中的代码，将对应的 API 密钥直接写入代码中。
修改之后启动运行即可。