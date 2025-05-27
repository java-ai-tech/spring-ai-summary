package com.glmapper.controller.chat;

import com.glmapper.controller.chat.service.MultiModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Classname MultiModelChatController
 * @Description MultiModelChatController
 * @Date 2025/5/23 18:23
 * @Created by glmapper
 */
@RestController
@RequestMapping("/api/multi-chat")
public class MultiModelChatController {

    @Autowired
    private MultiModelService multiModelService;

    @RequestMapping("/multiClientFlow")
    public void multiClientFlow() {
        multiModelService.multiClientFlow();
    }
}
