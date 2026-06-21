package com.glmapper.ai.chat.ollama.controller;

import com.glmapper.ai.chat.ollama.service.ChatMemoryService;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
import reactor.core.publisher.Flux;

/**
 * @Classname ChatController
 * @Description ollama ChatController
 * @Date 2025/6/6 08:26
 * @Created by Gepeng18111
 */
@RestController
@RequestMapping("/api/ollama")
public class ChatController {

    @Autowired
    private ChatClient chatClient;
    @Autowired
    private ChatMemoryService chatMemoryService;

    /**
     * 普通的聊天接口
     *
     * @param userInput 用户输入
     * @return 返回内容
     */
    @GetMapping("/chat")
    public String prompt(@RequestParam String userInput) {
        return this.chatClient.prompt()
                .user( userInput)
                .call()
                .content();
    }

    /**
     * 普通的聊天接口
     *
     * @param userInput 用户输入
     * @return 返回内容
     */
    @GetMapping("/chat-memory")
    public String prompt(@RequestParam String userInput,String conversationId) {
        return chatMemoryService.call(userInput,conversationId);
//        return this.chatClient.prompt("你是一个智能助手，请用自然语言形式回答，不要使用JSON格式或其他结构化格式")
//                .user("问题：" + userInput)
//                .call()
//                .content();
    }

    /**
     * 流式聊天接口
     *
     * @param userInput 用户输入
     * @return SseEmitter 流式响应
     */
    @GetMapping("/chat-stream")
    public SseEmitter promptStream(@RequestParam String userInput) {
        SseEmitter emitter = new SseEmitter(60000L); // 设置超时时间为60秒
        
        try {
            // 使用流式调用
            Flux<String> response = this.chatClient.prompt()
                .user(userInput)
                .stream()
                .content();
            
            // 订阅响应式流并发送到SSE
            response.subscribe(
                token -> {
                    try {
                        emitter.send(SseEmitter.event().data(token));
                    } catch (Exception e) {
                        emitter.completeWithError(e);
                    }
                },
                error -> {
                    emitter.completeWithError(error);
                },
                () -> {
                    emitter.complete();
                }
            );
        } catch (Exception e) {
            emitter.completeWithError(e);
        }
        
        return emitter;
    }
}
