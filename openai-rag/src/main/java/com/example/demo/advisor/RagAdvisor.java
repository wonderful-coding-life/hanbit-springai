package com.example.demo.advisor;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisor;
import org.springframework.ai.chat.client.advisor.api.StreamAdvisorChain;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class RagAdvisor implements CallAdvisor, StreamAdvisor {
    private final VectorStore vectorStore;

    // 요청 정보를 담은 ChatClientRequest 객체를 받아서 RAG 정보를 시스템 메시지로 추가한 요청 객체를 다음 체인으로 전달
    // 전달 전후에 로깅이나 모니터링 작업도 가능
    @Override
    public @NonNull ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, @NonNull CallAdvisorChain callAdvisorChain) {
        var question = chatClientRequest.prompt().getUserMessage().getText();
        if (question != null) {
            var prompt = chatClientRequest.prompt().augmentSystemMessage(getSystemMessage(question));
            var request = chatClientRequest.mutate().prompt(prompt).build();
            var response = callAdvisorChain.nextCall(request);
            return response;
        } else {
            return callAdvisorChain.nextCall(chatClientRequest);
        }
    }

    @Override
    public @NonNull Flux<ChatClientResponse> adviseStream(ChatClientRequest chatClientRequest, StreamAdvisorChain streamAdvisorChain) {
        var question = chatClientRequest.prompt().getUserMessage().getText();
        if (question != null) {
            var prompt = chatClientRequest.prompt().augmentSystemMessage(getSystemMessage(question));
            var request = chatClientRequest.mutate().prompt(prompt).build();
            return streamAdvisorChain.nextStream(request);
        } else {
            return streamAdvisorChain.nextStream(chatClientRequest);
        }
    }

    @Override
    public @NonNull String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 1;
    }

    public String getSystemMessage(String question) {
        var documents = vectorStore.similaritySearch(question);
        String context = documents.stream().map(Document::getText).collect(Collectors.joining("\n---\n"));
        return "다음은 사용자가 질문한 내용에 대한 참고 문서입니다:\n" + context;
    }
}
