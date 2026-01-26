package com.example.demo;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class ChatApplication implements ApplicationRunner {
    private final OpenAiChatModel chatModel;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        if (args.containsOption("message")) {
            List<String> values = args.getOptionValues("message");
            if (values != null && !values.isEmpty()) {
                String message = values.getFirst();
                String completions = chatModel.call(message);
                log.info("AI 답변: {}", completions);
            }
        }
    }
}
