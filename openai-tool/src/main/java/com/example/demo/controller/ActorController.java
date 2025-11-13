package com.example.demo.controller;

import com.example.demo.model.Actor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.text.MessageFormat;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ActorController {
    private final OpenAiChatModel chatModel;

    @GetMapping("/actors")
    public Actor getActor(@RequestParam("name") String name) {
        var converter = new BeanOutputConverter<>(Actor.class);
        var message = MessageFormat.format("""
                {0}의 최신 출연작 5편의 영화를 알려 줘. 특히 2025년에 나온 영화가 있다면 포함해 줘.
                {1}
                """, name, converter.getFormat());
        log.info("message = {}", message);
        var json = chatModel.call(message);
        return converter.convert(json);
    }
}
