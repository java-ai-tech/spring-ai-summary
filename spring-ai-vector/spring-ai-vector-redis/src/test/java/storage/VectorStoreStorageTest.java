package storage;

import com.glmapper.ai.vector.RedisVectorApplication;
import com.glmapper.ai.vector.storage.VectorStoreStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 测试 基于 redis 的 VectorStoreStorage 的存储和搜索功能
 *
 * @author GlassCat
 * @since 2025/6/7
 */
@SpringBootTest(
        classes = RedisVectorApplication.class,
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT
)
@ActiveProfiles("test")
public class VectorStoreStorageTest {

    @Autowired
    private VectorStoreStorage vectorStoreStorage;

    //prepare test data
    private static final List<Document> DOCS = List.of(
            new Document("Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!! Spring AI rocks!!", Map.of("test-data", "true")),
            new Document("The World is Big and Salvation Lurks Around the Corner", Map.of("test-data", "true")),
            new Document("You walk forward facing the past and you turn back toward the future.", Map.of("test-data", "true")));


    @AfterEach
    public void cleanUp() {
        // clear the vector store after each test
        Set<String> ids = DOCS.stream().map(Document::getId)
                .collect(Collectors.toSet());
        vectorStoreStorage.delete(ids);
    }

    @Test
    public void testStoreAndSearch() {
        // store documents
        vectorStoreStorage.store(DOCS);
        // do search
        String query = "Spring AI rocks!!";
        List<Document> results = vectorStoreStorage.search(query);
        // assertions
        Assertions.assertFalse(results.isEmpty(), "搜索结果不应该为空");
    }
}
