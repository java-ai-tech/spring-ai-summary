package com.glmapper.ai.tc.tools;

import org.springframework.ai.tool.annotation.Tool;

/**
 * @Classname FileReaderTools
 * @Description 读取文件工具
 * @Date 2025/5/29 15:06
 * @Created by glmapper
 */
public class FileReaderTools {

    @Tool(description = "Read a file and print its content")
    public String readFileAndPrint(String filePath) {
        String content = "";
        try {
            java.nio.file.Path path = java.nio.file.Paths.get(filePath);
            content = java.nio.file.Files.readString(path);
            System.out.println("File content:\n" + content);
        } catch (Exception e) {
            // 使用 classloader 读取 classpath 下的文件
            try {
                java.io.InputStream inputStream = getClass().getClassLoader().getResourceAsStream(filePath);
                if (inputStream != null) {
                    content = new String(inputStream.readAllBytes());
                    System.out.println("File content from classpath:\n" + content);
                }
            } catch (Exception ex) {
                content = "Error reading file from classpath: " + ex.getMessage();
                System.out.println("Error reading file from classpath: " + ex.getMessage());
            }
            System.out.println("Error reading file: " + e.getMessage());
        }
        return content;
    }
}
