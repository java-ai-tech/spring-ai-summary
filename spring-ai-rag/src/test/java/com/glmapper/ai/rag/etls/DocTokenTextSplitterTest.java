package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import com.glmapper.ai.rag.transformers.DocTokenTextSplitter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname DocTokenTextSplitterTest
 * @Description DocTokenTextSplitterTest
 * @Date 2025/6/4 10:42
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class DocTokenTextSplitterTest {

    @Autowired
    private DocTokenTextSplitter docTokenTextSplitter;

    @Test
    public void testSplitText() {
        var splitDocuments = docTokenTextSplitter.testSplitDocuments();
        for (Document doc : splitDocuments) {
            System.out.println("Chunk: " + doc.getText());
            System.out.println("Metadata: " + doc.getMetadata());
        }
        Assertions.assertTrue(splitDocuments.size() == 2, "分割后的文档数量应该等于2");
    }
}
