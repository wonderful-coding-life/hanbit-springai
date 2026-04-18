package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ApiController {
    private final OpenAiChatModel chatModel;
    private final OpenAiImageModel imageModel;

    @GetMapping("/api/chat")
    public String getChat(@RequestParam("message") String message) {
        return chatModel.call(message);
    }

    @GetMapping("/api/image")
    public String getImage(@RequestParam("message") String message) {
        ImageResponse response = imageModel.call(new ImagePrompt(message, OpenAiImageOptions.builder()
                .quality("hd")
                .width(1024)
                .height(1024).build()));
        return response.getResult().getOutput().getUrl();
    }
}
