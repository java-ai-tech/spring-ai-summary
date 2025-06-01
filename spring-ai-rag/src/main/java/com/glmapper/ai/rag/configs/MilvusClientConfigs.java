package com.glmapper.ai.rag.configs;

import io.milvus.client.MilvusClient;
import io.milvus.param.collection.LoadCollectionParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.milvus.MilvusVectorStore;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

/**
 * @Classname MilvusClientConfigs
 * @Description MilvusClientConfigs
 * @Date 2025/6/1 15:42
 * @Created by glmapper
 */
@Configuration
@Slf4j
public class MilvusClientConfigs implements InitializingBean {

    @Autowired
    private VectorStore vectorStore;

    @Override
    public void afterPropertiesSet() throws Exception {
        if (vectorStore instanceof MilvusVectorStore) {
            vectorStore.getNativeClient().ifPresent(client -> {
                ((MilvusClient) client).loadCollection(LoadCollectionParam.newBuilder()
                        .withCollectionName("vector_store")
                        .build());
                log.info("Milvus {} collection loaded successfully.", "vector_store");
            });
        }
    }
}
