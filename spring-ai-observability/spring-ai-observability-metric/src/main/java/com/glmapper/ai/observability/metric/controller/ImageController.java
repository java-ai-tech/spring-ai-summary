package com.glmapper.ai.observability.metric.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

@RestController
@RequestMapping("/observability/image")
public class ImageController {

    private final ImageModel imageModel;

    private static final String DEFAULT_PROMPT = "为人工智能生成一张富有科技感的图片！";

    public ImageController(ImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping
    public String image() {

        ImageResponse imageResponse = imageModel.call(new ImagePrompt(DEFAULT_PROMPT));

        return imageResponse.getResult().getOutput().getUrl();
    }
}
