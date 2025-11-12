package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.image.ImageGenerationMetadata;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.ai.openai.metadata.OpenAiImageGenerationMetadata;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

@SpringBootTest
public class OpenAiImageModelTests {
    private static final Logger log = LoggerFactory.getLogger(OpenAiImageModelTests.class);

    @Autowired
    private OpenAiImageModel imageModel;

    // 리얼리즘 사진 스타일
    String message1 = """
            화성 표면에서 탐사 로버가 움직이고 있으며, 그 옆에는 2족 보행 로봇이 함께 탐사 활동을 하고 있다.
            붉은 모래 언덕과 먼지 낀 하늘이 배경이며, 태양빛이 낮게 비추는 오후의 분위기.
            실제 사진처럼 보이는 고해상도 장면, 자연스러운 그림자와 질감.
            """;

    // 시네마틱 영화 장면 스타일
    String message2 = """
            화성 표면에서 탐사 로버가 움직이고 있으며, 그 옆에는 2족 보행 로봇이 함께 탐사 활동을 하고 있다.
            붉은 모래 언덕과 먼지 낀 하늘이 배경이며, 태양빛이 낮게 비추는 오후의 분위기.
            영화 포스터처럼 웅장하고 드라마틱한 구도.
            """;

    // 과학 다큐멘터리 스타일
    String message3 = """
            화성 탐사 현장을 다큐멘터리 사진처럼 표현.
            실제 NASA 탐사 사진처럼 로버의 금속 질감과 먼지 낀 렌즈 표현이 사실적이다.
            2족 보행 로봇이 로버 옆에서 탐사를 돕는 장면.
            """;

    @Test
    public void testImageModelSimple() {
        ImageResponse response = imageModel.call(new ImagePrompt(message1));
        log.info("URL {}", response.getResult().getOutput().getUrl());
    }

    // style 옵션은 "기본적인 톤의 방향"을 정하는 스위치, 프롬프트는 "세부 묘사나 분위기"를 조정 (photo realistic, cinematic lighting)
    // vivid - 색감과 조명, 디테일이 더 강하고 "화려한" 결과 — 포스터, 일러스트, SF 분위기
    // natural - 현실 사진처럼 자연스러운 색감과 질감 — 다큐멘터리, 제품 사진, 실사풍
    @Test
    public void testImageModel() throws IOException {
        OpenAiImageOptions openAiImageOptions = OpenAiImageOptions.builder()
                .model("dall-e-3") // dall-e-3, gpt-image-1-mini (protected - image input)
                .style("natural") // vivid (default), natural
                .quality("hd") // standard (default), hd
                .responseFormat("url") // url (default), b64_json
                .width(1024)
                .height(1024).build();

        ImagePrompt imagePrompt = new ImagePrompt(message1, openAiImageOptions);
        ImageResponse imageResponse = imageModel.call(imagePrompt);

        if (imageResponse.getResult().getOutput().getB64Json() != null) {
            // OpenAI와 통신은 JSON으로 이루어지고, JSON 내에서 바이너리를 표현하기 위해 Base64 인코딩을 함
            log.info("Base64Json {}", imageResponse.getResult().getOutput().getB64Json());
            byte[] imageBytes = Base64.getDecoder().decode(imageResponse.getResult().getOutput().getB64Json());
            // dall-e-3는 png 포맷만 지원, 다른 포맷이 필요하다면 애플리케이션에서 변환해야 함
            Files.write(Paths.get("D:\\archive\\image\\openai-image.png"), imageBytes);
        }

        if (imageResponse.getResult().getOutput().getUrl() != null) {
            log.info("Url {}", imageResponse.getResult().getOutput().getUrl());
        }

        ImageGenerationMetadata metadata = imageResponse.getResult().getMetadata();
        // Java 16+ 에서 적용된 패턴 매칭 문법으로 명시적으로 형변환(cast)을 하지 않더라도 open이라는 새로운 지역 변수를 만들어 줌
        if (metadata instanceof OpenAiImageGenerationMetadata open) {
            // OpenAI 모델이 실제 이미지 생성을 위해 내부적으로 재작성한 프롬프트 텍스트
            // 즉, 사용자가 입력한 프롬프트를 모델이 더 명확하고 구체적으로 이해하기 위해 수정
            String revised = open.getRevisedPrompt();
            log.info("revisedPrompt {} ", revised);
        }
    }
}
