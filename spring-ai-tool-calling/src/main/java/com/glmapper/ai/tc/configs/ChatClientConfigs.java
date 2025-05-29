package com.glmapper.ai.tc.configs;

import com.glmapper.ai.tc.tools.function.WeatherRequest;
import com.glmapper.ai.tc.tools.function.WeatherService;
import com.glmapper.ai.tc.tools.methods.DateTimeTools;
import com.glmapper.ai.tc.tools.methods.FileReaderTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;

/**
 * In all cases, you need to disable the ChatClient.Builder autoconfiguration by setting the property
 *
 * @Classname ChatClientConfigs
 * @Description ChatClientConfigs
 * @Date 2025/4/15 14:53
 * @Created by glmapper
 */
@Configuration
public class ChatClientConfigs {

    @Bean
    public ChatClient chatClient(OpenAiChatModel chatModel, ChatMemory chatMemory) {
//        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
//        ToolCallback toolCallback = MethodToolCallback.builder()
//                .toolDefinition(ToolDefinition.builder().name("getCurrentDateTime")
//                        .description("Get the current date and time in the user's timezone")
//                        .build())
//                .toolMethod(method)
//                .build();
//        ChatClient chatClient = ChatClient.builder(chatModel)
//                .defaultToolCallbacks(toolCallback)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
//                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
//                .build();



//        ToolCallback[] toolCallbacks = ToolCallbacks.from(new DateTimeTools(),new FileReaderTools());
//        ChatClient.builder(chatModel)
//                .defaultToolCallbacks(toolCallbacks)
//                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
//                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
//                .build();


        ToolCallback toolCallback = FunctionToolCallback
                .builder("currentWeather", new WeatherService())
                .description("Get the weather in location")
                .inputType(WeatherRequest.class)
                .build();

        // 与上面的代码等价
        return ChatClient.builder(chatModel)
                .defaultTools(new DateTimeTools(), new FileReaderTools())
                .defaultToolCallbacks(toolCallback)
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(chatMemory).build())
                .defaultSystem("You are deepseek chat bot, you answer questions in a concise and accurate manner.")
                .build();
    }
}
