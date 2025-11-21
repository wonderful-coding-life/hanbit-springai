package com.example.demo;

import com.example.demo.tool.ProductOrderTool;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

@SpringBootTest
public class ToolTests {
    private static final Logger log = LoggerFactory.getLogger(ToolTests.class);

    @Autowired
    private ChatModel chatModel;

    @Autowired
    private ProductOrderTool productOrderTool;

    private static final String context = """
            (1) 배송중인 상품은 취소할 수 없고, 배송 준비중인 상태에서만 취소가 가능해.
            (2) 주문 취소는 명시적으로 제품 이름과 함께 취소해 달라고 할 때만 취소해.
            (3) 답변은 짥고 명료하게 해 줘.
            """;

    @Test
    public void testTool() {
        //String question = "주문목록 알려 주세요.";
        String question = "오늘 날짜와 지금 시간을 알려 주세요.";
        List<Message> messages = List.of(
                new UserMessage(question),
                new SystemMessage(context)
        );
        ToolCallback[] productOrderTools = ToolCallbacks.from(productOrderTool);
        ChatOptions chatOptions = ToolCallingChatOptions.builder()
                .toolCallbacks(productOrderTools)
                .build();
        Prompt prompt = new Prompt(messages, chatOptions);
        ChatResponse response = chatModel.call(prompt);

        log.info("answer = {}", response.getResult().getOutput().getText());
    }

    @Test
    public void testSimple() {
        log.info("springboot = {}", chatModel.call("스프링부트에 대해 알려 주세요."));
    }
}
