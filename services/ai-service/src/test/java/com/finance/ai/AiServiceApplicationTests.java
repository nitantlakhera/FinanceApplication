package com.finance.ai;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

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
class AiServiceApplicationTests {

    @Test
    void contextLoads() {
    }
}
