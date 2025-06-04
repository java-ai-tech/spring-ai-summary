package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname JsonDocumentReaderTest
 * @Description JsonDocumentReaderTest
 * @Date 2025/6/3 18:36
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class JsonDocumentReaderTest {

    @Autowired
    private JsonDocumentReader jsonDocumentReader;

    @Test
    public void testReadJsonDocuments() {
        String filePath = "files/test.json";
        jsonDocumentReader.loadJsonAsDocuments(filePath);
    }
}
