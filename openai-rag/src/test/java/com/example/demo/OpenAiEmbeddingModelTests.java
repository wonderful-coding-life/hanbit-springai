package com.example.demo;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.embedding.Embedding;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import java.util.List;

@SpringBootTest
public class OpenAiEmbeddingModelTests {
    private static final Logger log = LoggerFactory.getLogger(OpenAiEmbeddingModelTests.class);

    @Autowired
    private OpenAiEmbeddingModel embeddingModel;

    private String text1 = """
            제품 사용 전 반드시 배터리를 완전히 충전해 주세요.
            전원 버튼을 3초간 길게 눌러 전원을 켜면 LED 표시등이 점등됩니다.
            먼지통이 가득 차면 흡입력이 떨어질 수 있으므로, 청소 후 반드시 먼지통과 필터를 세척하세요.
            필터는 완전히 건조된 후 다시 장착해야 하며, 젖은 상태로 사용할 경우 고장의 원인이 될 수 있습니다.
            배터리는 약 500회 충전이 가능합니다. 장시간 사용하지 않을 때는 완충 후 서늘한 곳에 보관하세요.
            """;
    private String text2 = """
            본 제품은 구입일로부터 1년간 무상 보증이 제공됩니다.
            단, 소비자 과실이나 천재지변으로 인한 손상은 보증 대상에서 제외됩니다.
            제품 수리 시, 서비스 센터 방문 또는 택배 접수가 가능합니다.
            교환 또는 환불은 구입 후 7일 이내, 제품 및 포장 상태가 완전할 경우에만 가능합니다.
            수리 완료 후에는 수리 내역서와 함께 보증기간이 자동으로 연장되지 않으며, 교체된 부품은 별도 보증되지 않습니다.
            """;
    private String text3 = """
            정규직 직원은 입사 후 3개월의 수습 기간을 거치며, 근무 성과 평가 결과에 따라 정규 전환이 결정됩니다.
            연차휴가는 근속연수에 따라 차등 부여되며, 사용하지 않은 휴가는 익년도 1월 말까지 이월 가능합니다.
            재택근무는 부서장 승인 하에 주 2회까지 허용됩니다.
            사내 교육 프로그램은 분기별로 운영되며, 필수 교육을 미이수할 경우 인사고과에 반영될 수 있습니다.
            퇴사 시에는 최소 30일 전에 인사팀에 서면으로 통보해야 합니다.
            """;
    private String message1 = "배터리 수명은 얼마나 되나요?";
    private String message2 = "사용자의 실수로 제품이 고장 났을 때 무상 수리가 가능한가요?";
    private String message3 = "신입사원은 수습 기간이 얼마나 되나요?";

    @Test
    public void testEmbeddingModelSimple() {
        log.info("dimemsion {}", embeddingModel.dimensions());
        float[] vector = embeddingModel.embed(message1);
        log.info("vector = {}", vector);
    }

    @Test
    public void testEmbeddingModelResponse() {
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text1, text2, text3));

        log.info("metadata.model = {}", response.getMetadata().getModel());
        log.info("metadata.usage.promptTokens = {}, generationTokens = {}, totalTokens = {}",
                response.getMetadata().getUsage().getPromptTokens(),
                response.getMetadata().getUsage().getCompletionTokens(),
                response.getMetadata().getUsage().getTotalTokens());

        for (Embedding embedding : response.getResults()) {
            float[] vector = embedding.getOutput();
            log.info("vector = {}", vector);
        }
    }
}
