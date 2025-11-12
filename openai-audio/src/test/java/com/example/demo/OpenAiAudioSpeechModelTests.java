package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.openai.OpenAiAudioSpeechModel;
import org.springframework.ai.openai.OpenAiAudioSpeechOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.openai.audio.speech.SpeechPrompt;
import org.springframework.ai.openai.audio.speech.SpeechResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@SpringBootTest
public class OpenAiAudioSpeechModelTests {
    private static final Logger log = LoggerFactory.getLogger(OpenAiAudioSpeechModelTests.class);

    @Autowired
    private OpenAiAudioSpeechModel speechModel;

    @Test
    public void testSpeechModelSimple() throws IOException {
        byte[] bin = speechModel.call("이번역은 선릉, 선릉역입니다. 내리실 문은 오른쪽 입니다. 멀티캠퍼스로 가실 분들은 이번역에서 내리시기 바랍니다.");
        Files.write(Paths.get("D:\\archive\\audio\\ai_tts_simple.mp3"), bin);
    }

    @Test
    public void testSpeechModelOptions() throws IOException {
        OpenAiAudioSpeechOptions speechOptions = OpenAiAudioSpeechOptions.builder()
                .model("tts-1-hd") // tts-1, tts-1-hd, gpt-4o-mini-tts (not ready yet for spring ai)
                .voice(OpenAiAudioApi.SpeechRequest.Voice.NOVA) // default ALLOY?
                .responseFormat(OpenAiAudioApi.SpeechRequest.AudioResponseFormat.MP3)
                .speed(1.0f) // fast > 1.0
                .build();

        String text = """
                안녕하세요, 고객님.
                문의 사항이 있으시면 '삐' 소리 후에 음성으로 남겨 주세요.
                확인 후 빠르게 연락드리겠습니다.
                """;

        String textEng = """
                Hello, this is Multicampus.
                If you have any questions, please leave a voice message after the beep.
                Thank you!
                """;

        SpeechPrompt speechPrompt = new SpeechPrompt(text, speechOptions);
        SpeechResponse response = speechModel.call(speechPrompt);
        log.info("Metadata {}", response.getMetadata());

        RateLimit rateLimit = response.getMetadata().getRateLimit();
        if (rateLimit != null) {
            log.info("requestLimit = {}, requestRemaining = {}, requestReset = {}",
                    rateLimit.getRequestsLimit(),
                    rateLimit.getRequestsRemaining(),
                    rateLimit.getRequestsReset());
            // 텍스트 토큰을 사용하지 않고 오디오 길이(초) 기준으로 과금되기 때문에 토큰 제한은 null이 반환
            log.info("tokensLimit = {}, tokensRemaining = {}, tokensReset = {}",
                    rateLimit.getTokensLimit(),
                    rateLimit.getTokensRemaining(),
                    rateLimit.getTokensReset());
        }

        byte[] bin = response.getResult().getOutput();
        Files.write(Paths.get("D:\\archive\\audio\\ai_tts_options.mp3"), bin);
    }
}
