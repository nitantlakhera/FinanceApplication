package com.finance.ai.web;

import com.finance.ai.dto.FraudCheckRequest;
import com.finance.ai.dto.SummarizeRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
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
class AiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void shouldReturnFraudCheckResponse() throws Exception {
        String json = """
                {
                  "orderId": "%s",
                  "userId": "%s",
                  "amount": 99.99,
                  "currency": "USD",
                  "paymentMethod": "CARD",
                  "userCountry": "US",
                  "merchantCountry": "US",
                  "userOrderCount": 5
                }
                """.formatted(UUID.randomUUID(), UUID.randomUUID());

        mockMvc.perform(post("/ai/fraud-check")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.riskLevel").value("LOW"));
    }

    @Test
    void shouldReturnOrderSummary() throws Exception {
        String json = """
                {
                  "orderId": "%s",
                  "orderStatus": "COMPLETED",
                  "paymentStatus": "PAID",
                  "inventoryStatus": "IN_STOCK"
                }
                """.formatted(UUID.randomUUID());

        mockMvc.perform(post("/ai/summarize-order")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SUCCESS"));
    }
}
