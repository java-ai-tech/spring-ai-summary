package com.glmapper.ai.rag.etls;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.TextReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname TextDocumentReader
 * @Description TextDocumentReader
 * @Date 2025/6/3 19:12
 * @Created by glmapper
 */
@Component
public class TextDocumentReader {

    List<Document> loadText(String filePath) {
        Resource resource = new ClassPathResource(filePath);
        TextReader textReader = new TextReader(resource);
        // getCustomMetadata 返回一个可变的映射（map），可用于为文档添加自定义元数据。
        textReader.getCustomMetadata().put("filename", "test.txt");
        List<Document> docs = textReader.read();
        return docs;
    }

}
