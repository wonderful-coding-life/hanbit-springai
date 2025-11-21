package com.example.demo;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.OllamaEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class OllamaApplication implements ApplicationRunner {
    @Autowired
    private OllamaChatModel chatModel;

    @Autowired
    private OllamaEmbeddingModel embeddingModel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
//        log.info("{}", chatModel.call("서울 올림픽은 몇회 올림픽이야? 한국어로 답변해 줘."));
//        log.info("{}", embeddingModel.embed("우리나라는 살기 좋은 나라이다."));
    }
}
