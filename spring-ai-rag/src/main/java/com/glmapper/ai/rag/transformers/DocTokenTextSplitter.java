package com.glmapper.ai.rag.transformers;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * @Classname DocTokenTextSplitter
 * @Description DocTokenTextSplitter
 * @Date 2025/6/4 10:30
 * @Created by glmapper
 */
@Component
public class DocTokenTextSplitter {

    /**
     * 使用默认的 TokenTextSplitter 来分割文档
     *
     * @param documents 输入的文档列表
     * @return 分割后的文档列表
     */
    public List<Document> splitDocuments(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(documents);
    }

    /**
     * 使用自定义的 TokenTextSplitter 来分割文档
     * 这里设置了最大 token 数量为 1000，重叠 token 数量为 400，最小 token 数量为 10，最大字符数为 5000，启用分割
     *
     * @param documents 输入的文档列表
     * @return 分割后的文档列表
     */
    public List<Document> splitCustomized(List<Document> documents) {
        TokenTextSplitter splitter = new TokenTextSplitter(1000, 400, 10, 5000, true);
        return splitter.apply(documents);
    }

    /**
     * 测试方法，演示如何使用 TokenTextSplitter 来分割文档
     */
    public List<Document> testSplitDocuments() {
        Document doc1 = new Document("This is a long piece of text that needs to be split into smaller chunks for processing.", Map.of("source", "example.txt"));
        Document doc2 = new Document("Another document with content that will be split based on token count.", Map.of("source", "example2.txt"));

        TokenTextSplitter splitter = new TokenTextSplitter();
        return splitter.apply(List.of(doc1, doc2));
    }
}
