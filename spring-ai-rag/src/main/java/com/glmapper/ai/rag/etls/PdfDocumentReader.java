package com.glmapper.ai.rag.etls;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.ExtractedTextFormatter;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.reader.pdf.ParagraphPdfDocumentReader;
import org.springframework.ai.reader.pdf.config.PdfDocumentReaderConfig;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname PdfDocumentReader
 * @Description PdfDocumentReader
 * @Date 2025/6/4 09:49
 * @Created by glmapper
 */
@Component
public class PdfDocumentReader {

    /**
     * PagePdfDocumentReader 是依赖 Apache PdfBox 来解析 pdf
     *
     * @return
     */
    public List<Document> getDocsFromPdf() {
        PagePdfDocumentReader pdfReader = new PagePdfDocumentReader("classpath:files/test_page.pdf", PdfDocumentReaderConfig.builder()
                // 设置页面顶部边距为 0
                .withPageTopMargin(0)
                // 设置提取的文本格式化器，删除顶部的文本行
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                // 设置每个文档的页面数为 1
                .withPagesPerDocument(1).build());
        return pdfReader.read();
    }

    /**
     * 该方法使用 ParagraphPdfDocumentReader 来读取 PDF 文档中的段落。
     * <p>
     * 使用 PDF 目录（例如目录 TOC）信息将输入的 PDF 拆分为文本段落，并为每个段落输出一个单独的 Document
     *
     * @return
     */
    public List<Document> getDocsFromPdfWithCatalog() {
        ParagraphPdfDocumentReader pdfReader = new ParagraphPdfDocumentReader("classpath:files/test_paragraph.pdf", PdfDocumentReaderConfig.builder()
                // 设置页面顶部边距为 0
                .withPageTopMargin(0)
                // 设置提取的文本格式化器，删除顶部的文本行
                .withPageExtractedTextFormatter(ExtractedTextFormatter.builder()
                        .withNumberOfTopTextLinesToDelete(0)
                        .build())
                // 设置每个文档的页面数为 1
                .withPagesPerDocument(1).build());
        return pdfReader.read();
    }

}
