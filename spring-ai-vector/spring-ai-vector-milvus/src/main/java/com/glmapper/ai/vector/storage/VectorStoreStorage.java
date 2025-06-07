package com.glmapper.ai.vector.storage;

import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname VectorStoreStorage
 * @Description VectorStoreStorage
 * @Date 2025/6/7 15:31
 * @Created by glmapper
 */
@Component
public class VectorStoreStorage {

    @Autowired
    private VectorStore vectorStore;

    public void delete(List<String> idList) {
        vectorStore.delete(idList);
    }

    public void store(List<Document> documents) {
        if (documents == null || documents.isEmpty()) {
            return;
        }
        vectorStore.add(documents);
    }

    public List<Document> search(String query) {
        return vectorStore.similaritySearch(SearchRequest.builder()
                .query(query)
                .topK(5)
                .similarityThreshold(0.7)
                .build());
    }
}
