package com.glmapper.ai.rag.etls;

import com.glmapper.ai.rag.RagApplication;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * @Classname PdfDocumentReaderTest
 * @Description PdfDocumentReaderTest
 * @Date 2025/6/4 10:06
 * @Created by glmapper
 */
@SpringBootTest(classes = RagApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PdfDocumentReaderTest {

    @Autowired
    private PdfDocumentReader pdfDocumentReader;

    @Test
    public void testLoadPdf() {
        List<Document> pageDocs = pdfDocumentReader.getDocsFromPdf();
        Assertions.assertTrue(pageDocs.size() > 0, "PDF文档应该包含至少一个页面");
        List<Document> paragraphDocs = pdfDocumentReader.getDocsFromPdfWithCatalog();
        Assertions.assertTrue(paragraphDocs.size() > 0, "PDF文档应该包含至少一个段落");
    }
}
