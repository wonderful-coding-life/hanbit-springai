package com.example.demo.controller;

import com.example.demo.tool.ProductOrderTool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.support.ToolCallbacks;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {
    private final ChatModel chatModel;
    private final ProductOrderTool productOrderTool;
    private final ChatMemory chatMemory;

    private static final String systemMessage = """
            (1) 배송중인 상품은 취소할 수 없고, 배송 준비중인 상태에서만 취소가 가능해.
            (2) 주문 취소는 명시적으로 제품 이름과 함께 취소해 달라고 할 때만 취소해.
            (3) 답변은 짥고 명료하게 해 줘.
            """;

    @RequestMapping("/chat/tool")
    public String getChat(@RequestParam("message") String userMessage) {
        List<Message> messages = List.of(
                new UserMessage(userMessage),
                new SystemMessage(systemMessage)
        );
        ToolCallback[] productOrderTools = ToolCallbacks.from(productOrderTool);
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(productOrderTools)
                .build();
        Prompt prompt = new Prompt(messages, chatOptions);
        ChatResponse response = chatModel.call(prompt);
        return response.getResult().getOutput().getText();
    }

    @GetMapping("/chat/memory")
    public String getChat(@RequestParam("id") String id, @RequestParam("message") String message) {

        // 채팅 시작시 시스템 메시지 추가되며 윈도우 사이즈가 넘어가더라도 유지
        // 문서에 따르면 SystemMessage가 추가되면 기존 SystemMessage들은 모두 삭제
        if (chatMemory.get(id).isEmpty()) {
            chatMemory.add(id, new SystemMessage("정확하고 명료하게 답변 해 주세요"));
        }

        // 사용자가 전달한 유저 메시지 추가
        UserMessage userMessage = new UserMessage(message);
        chatMemory.add(id, userMessage);

        // 챗메모리에 있는 메시지로 프롬프트 구성하고 대화형 AI 모델 호출
        Prompt prompt = new Prompt(chatMemory.get(id));
        ChatResponse chatResponse = chatModel.call(prompt);

        // AI 모델 응답을 챗메모리에 추가
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        chatMemory.add(id, assistantMessage);

        // 필요시 로그로 확인
        log.info("chatMemory size = {}", chatMemory.get(id).size());
        List<Message> messages = chatMemory.get(id);
        messages.forEach(msg -> log.info("{}", msg.getText()));

        return assistantMessage.getText();
    }

    @GetMapping(value = "/chat/stream", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public Flux<String> getChatResponse(@RequestParam("message") String message) {
        log.info("Chat message: {}", message);
        return chatModel.stream(message);
    }
}
