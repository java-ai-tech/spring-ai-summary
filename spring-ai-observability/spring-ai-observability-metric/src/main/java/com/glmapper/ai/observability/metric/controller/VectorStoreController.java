package com.glmapper.ai.observability.metric.controller;


import com.glmapper.ai.observability.metric.storage.VectorStoreStorage;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/observability/vector")
public class VectorStoreController {

    @Autowired()
    private VectorStoreStorage vectorStoreStorage;

    @GetMapping("/store")
    public String embedding(@RequestParam String text) {
        Document document = new Document(text, Map.of("test-data", "true"));
        List<Document> documents = List.of(document);
        vectorStoreStorage.store(documents);
        return document.getId();
    }

    @GetMapping("/search")
    public List<Document> search(@RequestParam String text) {
        return vectorStoreStorage.search(text);
    }

    @GetMapping("/delete")
    public void delete(@RequestParam String id) {
        vectorStoreStorage.delete(Set.of(id));
    }

}
