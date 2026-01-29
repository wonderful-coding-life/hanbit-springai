package com.example.demo.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class RagController {
    private final ChatClient chatClient;

    @PostMapping("/api/chats")
    public String postMessage(@RequestParam("conversationId") String conversationId, @RequestParam("message") String message) {
        // 컨트롤러에서 ChatMemory, RAG 관련 로직을 분리하고 ChatClient와 Advisor에서 처리하도록 변경
        return chatClient.prompt()
                .user(message)
                // 각 advisor에서 conversationId를 참조할 수 있도록 설정
                .advisors(advisorSpec -> advisorSpec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call()
                .content();
    }
}
