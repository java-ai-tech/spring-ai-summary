package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname MdDocumentReaderTest
 * @Description MdDocumentReaderTest
 * @Date 2025/6/3 20:23
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MdDocumentReaderTest {

    @Autowired
    private MdDocumentReader mdDocumentReader;

    @Test
    public void testLoadMarkdown() {
        String filePath = "files/test.md";
        List<Document> docs = this.mdDocumentReader.loadMarkdown(filePath);
        Assertions.assertTrue(docs.size() > 1, "文档数量应该大于1");
    }
}
