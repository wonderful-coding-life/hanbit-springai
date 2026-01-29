package com.example.demo.config;

import com.example.demo.advisor.LogAdvisor;
import com.example.demo.advisor.RagAdvisor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ChatClientConfig {
    @Bean
    public ChatClient chatClient(ChatClient.Builder builder, LogAdvisor logAdvisor, RagAdvisor ragAdvisor, MessageChatMemoryAdvisor messageChatMemoryAdvisor) {
        return builder.defaultAdvisors(logAdvisor, ragAdvisor, messageChatMemoryAdvisor)
                .build();
    }

    @Bean
    public MessageChatMemoryAdvisor messageChatMemoryAdvisor(ChatMemory chatMemory) {
        return MessageChatMemoryAdvisor.builder(chatMemory).order(-99).build();
    }
}
