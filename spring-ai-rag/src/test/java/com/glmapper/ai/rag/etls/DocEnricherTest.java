package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import com.glmapper.ai.rag.transformers.DocEnricher;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname DocEnricherTest
 * @Description DocEnricherTest
 * @Date 2025/6/4 10:57
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocEnricherTest {
    @Autowired
    private DocEnricher docEnricher;
    @Autowired
    private MdDocumentReader mdDocumentReader;

    @Test
    public void testEnrichKeyword() {
        List<Document> documents = mdDocumentReader.loadMarkdown("files/test.md");
        // 使用 DocEnricher 来丰富文档的关键词和摘要
        this.docEnricher.enrichKeyword(documents).forEach(doc -> {
            System.out.println("Document ID: " + doc.getId());
            // 官方文档中的 “keywords” 是错的，实际是 excerpt_keywords
            System.out.println("Keywords: " + doc.getMetadata().get("excerpt_keywords"));
        });
        // 使用 DocEnricher 来丰富文档的摘要
        this.docEnricher.enrichSummary(documents).forEach(doc -> {
            System.out.println("Document ID: " + doc.getId());
            // 官方文档中的 “summary” 是错的，实际是 section_summary
            System.out.println("Summary: " + doc.getMetadata().get("section_summary"));
        });
    }

}
