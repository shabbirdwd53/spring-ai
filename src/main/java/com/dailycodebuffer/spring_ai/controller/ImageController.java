package com.dailycodebuffer.spring_ai.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ImageController {

    private final ChatModel chatModel;
    private final ImageModel imageModel;

    public ImageController(ChatModel chatModel, ImageModel imageModel) {
        this.chatModel = chatModel;
        this.imageModel = imageModel;
    }

    @GetMapping("image-to-text")
    public String describeImage() {
        String response = ChatClient.create(chatModel)
                .prompt()
                .user(useSpec ->
                        useSpec.text("Explain what you see in this Image")
                                .media(MimeTypeUtils.IMAGE_JPEG,
                                        new ClassPathResource("images/horse-8209533_1280.jpg")))
                .call()
                .content();

        return response;
    }


    @GetMapping("/image/{prompt}")
    public String generateImage(@PathVariable String prompt) {

        ImageResponse response = imageModel.call(
                new ImagePrompt(prompt,
                        OpenAiImageOptions
                                .builder()
                                .withN(1)
                                .withWidth(1024)
                                .withHeight(1024)
                                .withQuality("hd")
                                .build())
        );

        return response.getResult().getOutput().getUrl();
    }
}
