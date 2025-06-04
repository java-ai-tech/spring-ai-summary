package com.glmapper.ai.rag.chunks;

import io.milvus.client.MilvusServiceClient;
import io.milvus.param.collection.LoadCollectionParam;
import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * @Classname TextSplitter
 * @Description LangChainTextSplitter
 * @Date 2025/5/30 23:13
 * @Created by glmapper
 */
@Component
public class LangChainTextSplitter {

    @Autowired
    private VectorStore vectorStore;

    /**
     * 这里仅提供一个示例，实际使用时请根据需要修改路径和文件内容
     */
    public void embedding() {
        try {
            TextSplitter splitter = new TokenTextSplitter();
            URL path = LangChainTextSplitter.class.getClassLoader().getResource("classpath:files/test.md");
            String mdContent = Files.readString(Paths.get(path.toURI()), StandardCharsets.UTF_8);
            Document doc = new Document(mdContent);
            List<Document> docs = splitter.split(doc);
            this.vectorStore.add(docs);
            ((MilvusServiceClient) this.vectorStore.getNativeClient()
                    .get()).loadCollection(LoadCollectionParam.newBuilder().withCollectionName("vector_store").build());
            System.out.println("添加成功");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
