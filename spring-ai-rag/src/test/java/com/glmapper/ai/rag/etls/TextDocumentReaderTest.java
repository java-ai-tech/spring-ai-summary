package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname TextDocumentReaderTest
 * @Description TODO
 * @Date 2025/6/3 19:21
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class TextDocumentReaderTest {

    @Autowired
    private TextDocumentReader textDocumentReader;

    @Test
    public void testLoadText() {
        String filePath = "files/test.txt";
        List<Document> docs = textDocumentReader.loadText(filePath);
        Assertions.assertTrue(docs.size() > 0, "文档列表不应该为空");
        Document document = docs.get(0);
        Assertions.assertNotNull(document.isText(), "不是 Text 类型");
        Assertions.assertEquals("test.txt", document.getMetadata().get("filename"), "文件名元数据不正确");
    }

}
