package com.finance.ai.service;

import com.finance.ai.dto.ChatRequest;
import com.finance.ai.dto.FraudCheckRequest;
import com.finance.ai.dto.SummarizeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.math.BigDecimal;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "spring.autoconfigure.exclude=org.springframework.boot.autoconfigure.kafka.KafkaAutoConfiguration," +
                "org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration," +
                "org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration," +
                "org.springframework.boot.autoconfigure.orm.jpa.HibernateJpaAutoConfiguration," +
                "org.springframework.ai.autoconfigure.vectorstore.pgvector.PgVectorStoreAutoConfiguration," +
                "org.springframework.ai.autoconfigure.openai.OpenAiAutoConfiguration," +
                "org.springframework.ai.autoconfigure.ollama.OllamaAutoConfiguration"
})
class AiServiceIntegrationTest {

    @Autowired
    private FraudCheckService fraudCheckService;

    @Autowired
    private OrderSummaryService orderSummaryService;

    @Autowired
    private NaturalLanguageQueryService naturalLanguageQueryService;

    @Autowired
    private AiModelRouter modelRouter;

    @Test
    void contextLoadsAndFallbackProviderIsActive() {
        assertThat(modelRouter.getProvider()).isEqualTo("fallback");
    }

    @Test
    void fraudCheckReturnsLowRiskFallback() {
        FraudCheckRequest request = new FraudCheckRequest(
                UUID.randomUUID(),
                UUID.randomUUID(),
                BigDecimal.valueOf(99.99),
                "USD",
                "CARD",
                "US",
                "US",
                5
        );

        var response = fraudCheckService.checkFraud(request);

        assertThat(response.riskLevel()).isEqualToIgnoringCase("LOW");
        assertThat(response.recommendedBlock()).isFalse();
    }

    @Test
    void orderSummaryReturnsFallbackSummary() {
        SummarizeRequest request = new SummarizeRequest(
                UUID.randomUUID(),
                "COMPLETED",
                "PAID",
                "IN_STOCK"
        );

        var response = orderSummaryService.summarize(request);

        assertThat(response.summary()).containsIgnoringCase("summary");
    }

    @Test
    void naturalLanguageQueryReturnsFallbackAnswer() {
        ChatRequest request = new ChatRequest("What is the refund policy?", null);

        var response = naturalLanguageQueryService.ask(request);

        assertThat(response.answer()).containsIgnoringCase("fallback");
    }
}
