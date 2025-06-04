package com.glmapper.ai.rag.etls;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname MdDocumentReader
 * @Description MarkdownDocumentReader
 * @Date 2025/6/3 20:20
 * @Created by glmapper
 */
@Component
public class MdDocumentReader {

    public List<Document> loadMarkdown(String filePath) {
        MarkdownDocumentReaderConfig config = MarkdownDocumentReaderConfig.builder()
                .withHorizontalRuleCreateDocument(true)
                .withIncludeCodeBlock(false)
                .withIncludeBlockquote(false)
                .withAdditionalMetadata("filename", "test.md")
                .build();

        Resource resource = new ClassPathResource(filePath);
        MarkdownDocumentReader reader = new MarkdownDocumentReader(resource, config);
        return reader.get();
    }
}
