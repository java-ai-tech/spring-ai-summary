package com.glmapper.ai.rag.configs;

import org.springframework.ai.model.transformer.SummaryMetadataEnricher;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * @Classname EnricherConfig
 * @Description EnricherConfig
 * @Date 2025/6/4 10:56
 * @Created by glmapper
 */
@Configuration
public class EnricherConfig {

    @Bean
    public SummaryMetadataEnricher summaryMetadata(OpenAiChatModel chatModel) {
        return new SummaryMetadataEnricher(chatModel, List.of(SummaryMetadataEnricher.SummaryType.PREVIOUS, SummaryMetadataEnricher.SummaryType.CURRENT, SummaryMetadataEnricher.SummaryType.NEXT));
    }
}
