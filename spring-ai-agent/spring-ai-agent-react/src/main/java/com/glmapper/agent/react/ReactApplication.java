package com.glmapper.agent.react;

import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.glmapper.agent.react.tools.DateTimeTools;
import jakarta.annotation.PostConstruct;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

/**
 * @Classname ReactApplication
 * @Description TODO
 * @Date 2/6/26 11:34 AM
 * @Created by glmapper
 */
@SpringBootApplication
public class ReactApplication {

    @Autowired
    private ChatClient chatClient;

    public static void main(String[] args) throws GraphRunnerException {
        SpringApplication.run(ReactApplication.class, args);
    }

    @PostConstruct
    public void init() throws GraphRunnerException {
        System.out.println("ChatClient initialized: " + chatClient);

        Method method = ReflectionUtils.findMethod(DateTimeTools.class, "getCurrentDateTime");
        ToolCallback toolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinitions.builder(method)
                        .description("Get the current date and time in the user's timezone")
                        .build())
                .toolMethod(method)
                .toolObject(new DateTimeTools())
                .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder().name("weather_agent").chatClient(chatClient)
                .tools(toolCallback)
                .systemPrompt("You are a helpful assistant").saver(new MemorySaver()).build();

        // 运行 agent
        AssistantMessage response = agent.call("今天是几号，星期几？天气如何？合肥有什么好玩的？");
        System.out.println(response.getText());
    }
}
