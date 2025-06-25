package com.glmapper.ai.observability.metric.controller;


import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/observability/embedding")
public class EmbeddingController {

    private final EmbeddingModel embeddingModel;

    public EmbeddingController(EmbeddingModel embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @GetMapping
    public String embedding() {
        var embeddings = embeddingModel.embed("hello world.");
        return "embedding vector size:" + embeddings.length;
    }

    @GetMapping("/generic")
    public String embeddingGenericOpts() {

        var embeddings = embeddingModel.call(new EmbeddingRequest(
                List.of("hello world."),
                OpenAiEmbeddingOptions.builder().model("text-embedding-v4").build())
        ).getResult().getOutput();
        return "embedding vector size:" + embeddings.length;
    }

}
