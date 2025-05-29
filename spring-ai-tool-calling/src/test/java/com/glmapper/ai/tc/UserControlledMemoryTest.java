package com.glmapper.ai.tc;

import com.glmapper.ai.tc.memory.UserControlledMemory;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @Classname UserControlledMemoryTest
 * @Description TODO
 * @Date 2025/5/29 21:04
 * @Created by glmapper
 */
@SpringBootTest(classes = ToolCallingApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class UserControlledMemoryTest {

    @Autowired
    private UserControlledMemory userControlledMemory;

    @Test
    public void testUserControlledMemory() {
        String answer = this.userControlledMemory.testChat("");
        System.out.println("AI回答: " + answer);
    }
}
