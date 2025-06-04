package com.glmapper.ai.rag.etls;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.jsoup.JsoupDocumentReader;
import org.springframework.ai.reader.jsoup.config.JsoupDocumentReaderConfig;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname HtmlDocumentReader
 * @Description HtmlDocumentReader
 * @Date 2025/6/3 19:45
 * @Created by glmapper
 */
@Component
public class HtmlDocumentReader {

    List<Document> loadHtml(String filePath) {
        JsoupDocumentReaderConfig config = JsoupDocumentReaderConfig.builder()
                .selector("article p") // Extract paragraphs within <article> tags
                .charset("ISO-8859-1")  // Use ISO-8859-1 encoding
                .includeLinkUrls(true) // Include link URLs in metadata
                .metadataTags(List.of("author", "date")) // Extract author and date meta tags
                .additionalMetadata("source", "test.html") // Add custom metadata
                .build();
        Resource resource = new ClassPathResource(filePath);
        JsoupDocumentReader reader = new JsoupDocumentReader(resource, config);
        return reader.get();
    }
}
