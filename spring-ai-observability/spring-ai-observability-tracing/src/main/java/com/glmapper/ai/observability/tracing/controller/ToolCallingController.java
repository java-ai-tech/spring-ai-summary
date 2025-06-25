package com.glmapper.ai.observability.tracing.controller;

import com.glmapper.ai.observability.tracing.tools.function.WeatherRequest;
import com.glmapper.ai.observability.tracing.tools.function.WeatherService;
import com.glmapper.ai.observability.tracing.tools.methods.DateTimeTools;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/observability/tools")
public class ToolCallingController {

	public final ChatClient chatClient;

	public ToolCallingController(ChatClient.Builder builder) {
		this.chatClient = builder.build();
	}

	@GetMapping("/function")
	public String functionTools() {
		ToolCallback toolCallback = FunctionToolCallback
				.builder("currentWeather", new WeatherService())
				.description("Get the weather in location")
				.inputType(WeatherRequest.class)
				.build();

		return chatClient
				.prompt("上海的天气怎么样？")
				.toolCallbacks(toolCallback)
				.call()
				.content();
	}

	// 记录 tools 观测数据
	@GetMapping("/method")
	public String methodTools() {
		return chatClient
				.prompt("今天是几号？")
				.tools(new DateTimeTools())
				.call()
				.content();
	}

}
