package com.glmapper.ai.vector.storage;

import com.glmapper.ai.vector.MilvusVectorApplication;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

/**
 * @Classname VectorStoreStorageTest
 * @Description 测试 基于 milvus 的 VectorStoreStorage 的存储和搜索功能
 * @Date 2025/6/7 15:34
 * @Created by glmapper
 */
@SpringBootTest(classes = MilvusVectorApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class VectorStoreStorageTest {

    @Autowired
    private VectorStoreStorage vectorStoreStorage;
    //准备测试数据
    public static List<Document> documents = List.of(
            new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("meta1", "meta1")),
            new Document("The World is Big and Salvation Lurks Around the Corner"),
            new Document("You walk forward facing the past and you turn back toward the future.", Map.of("meta2", "meta2")));


    @AfterEach
    public void cleanUp() {
        // 清理测试数据
        List<String> ids = documents.stream().map(Document::getId).toList();
        vectorStoreStorage.delete(ids);
    }

    @Test
    public void testStoreAndSearch() {
        // 存储文档
        vectorStoreStorage.store(documents);
        // 执行搜索
        String query = "Spring AI rocks!!";
        List<Document> results = vectorStoreStorage.search(query);
        // 输出搜索结果
        Assertions.assertFalse(results.isEmpty(), "搜索结果不应该为空");
    }
}
