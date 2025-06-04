package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname HtmlDocumentReaderTest
 * @Description HtmlDocumentReaderTest
 * @Date 2025/6/3 20:01
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class HtmlDocumentReaderTest {

    @Autowired
    private HtmlDocumentReader htmlDocumentReader;

    @Test
    public void testReadHtmlDocuments() {
        String filePath = "files/test.html";
        List<Document> docs = htmlDocumentReader.loadHtml(filePath);
        Document document = docs.get(0);
        Assertions.assertEquals("glmapper", document.getMetadata().get("author"));
        Assertions.assertTrue(document.getText().contains("main content"));
    }
}
