package com.glmapper.ai.rag.etls;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Classname JsonDocumentReader
 * @Description JsonDocumentReader
 * @Date 2025/6/3 18:10
 * @Created by glmapper
 */
@Component
public class JsonDocumentReader {


    /**
     * JsonReader 构造函数参数：
     * <p>
     * 1、`resource`：指向 JSON 文件的 Spring `Resource` 对象。
     * 2、`jsonKeysToUse`：一个字符串数组，指定从 JSON 中提取哪些键作为生成的 `Document` 对象的文本内容。
     * 3、`jsonMetadataGenerator`：可选的 `JsonMetadataGenerator`，用于为每个 `Document` 生成元数据。
     * </p>
     * <p>
     * 加载JSON文件并转换为文档列表
     *
     * @return
     */
    public List<Document> loadJsonAsDocuments(String filePath) {
        Resource resource = new ClassPathResource(filePath);
        // 1、使用 JsonReader 读取 JSON 文件， 默认读取整个 JSON 文件内容作为文档文本。
        JsonReader jsonReader = new JsonReader(resource, "", "");
        List<Document> docs = jsonReader.get();
        String formattedContent = docs.get(0).getFormattedContent();
        System.out.println("default; content: \n" + formattedContent);
        // 2、使用 JsonReader 读取 JSON 文件，指定 jsonKeysToUse 参数来提取特定的键作为文档文本。
        jsonReader = new JsonReader(resource, "choices", "");
        docs = jsonReader.get();
        System.out.println("JsonReader with jsonKeysToUse; content: \n" + docs.get(0).getFormattedContent());
        // 3、使用 JsonReader 读取 JSON 文件，指定 jsonKeysToUse 参数为多个键。
        jsonReader = new JsonReader(resource, "choices", "content");
        docs = jsonReader.get();
        System.out.println("JsonReader with multi jsonKeysToUse; content: \n" + docs.get(0).getFormattedContent());
        // 4、使用 JsonReader 读取 JSON 文件，使用 JSON point 来指定提取的内容。
        jsonReader = new JsonReader(resource, "", "");
        docs = jsonReader.get("/choices");
        System.out.println("JsonReader with pointer[choices]; content: \n" + docs.get(0).getFormattedContent());
        return docs;
    }
}
