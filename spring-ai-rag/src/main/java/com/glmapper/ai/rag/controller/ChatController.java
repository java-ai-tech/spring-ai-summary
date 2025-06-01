package com.glmapper.ai.rag.controller;

import com.glmapper.ai.rag.chunks.LangChainTextSplitter;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.SystemPromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Classname ChatController
 * @Description qwen ChatController
 * @Date 2025/5/28 13:51
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/qwen")
public class ChatController {

    @Autowired
    private ChatClient chatClient;

    @Autowired
    private LangChainTextSplitter langChainTextSplitter;

    @Autowired
    private VectorStore vectorStore;

    /**
     * 初始化数据
     * @return
     */
    @GetMapping("embedding_test")
    public String embedding() {
        try {
            langChainTextSplitter.embedding();
            return "Embedding completed successfully.";
        } catch (Exception e) {
            return "Embedding failed: " + e.getMessage();
        }
    }

    /**
     * 普通的聊天接口
     *
     * @param userInput 用户输入
     * @return 返回内容
     */
    @GetMapping("/chat")
    public String prompt(@RequestParam String userInput) {
        Message userMessage = new UserMessage(userInput);
        List<Document> similarDocuments = vectorStore.similaritySearch(userInput);
        String systemMessageString = "You are a helpful assistant. Here are some relevant documents:\n\n {documents}";
        SystemPromptTemplate systemPromptTemplate = new SystemPromptTemplate(systemMessageString);
        String tncString = similarDocuments.stream()
                .map(Document::getFormattedContent)
                .collect(Collectors.joining("\n"));
        Message systemMessage = systemPromptTemplate.createMessage(Map.of("documents", tncString));
        Prompt prompt = new Prompt(List.of(systemMessage, userMessage));
        String content = chatClient.prompt(prompt).call().content();
        return content;
    }
}
