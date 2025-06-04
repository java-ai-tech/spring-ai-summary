package com.glmapper.ai.rag.transformers;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.document.Document;
import org.springframework.ai.model.transformer.KeywordMetadataEnricher;
import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname DocEnricher
 * @Description DocEnricher
 * @Date 2025/6/4 10:54
 * @Created by glmapper
 */
@Component
public class DocEnricher {

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private SummaryMetadataEnricher enricher;

    /**
     * 使用 KeywordMetadataEnricher 来丰富文档的元数据
     * 这里设置了最大关键词数量为 5
     *
     * @param documents 输入的文档列表
     * @return 丰富后的文档列表
     */
    public List<Document> enrichKeyword(List<Document> documents) {
        KeywordMetadataEnricher enricher = new KeywordMetadataEnricher(this.chatModel, 5);
        return enricher.apply(documents);
    }


    /**
     * 使用 SummaryMetadataEnricher 来丰富文档的摘要元数据
     * 这里使用了默认的摘要类型
     *
     * @param documents 输入的文档列表
     * @return 丰富后的文档列表
     */
    public List<Document> enrichSummary(List<Document> documents) {
        return this.enricher.apply(documents);
    }
}
