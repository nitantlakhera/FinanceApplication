package com.finance.ai.service;

import com.finance.ai.dto.FraudCheckRequest;
import com.finance.ai.dto.FraudCheckResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class FraudCheckService {

    private static final Logger log = LoggerFactory.getLogger(FraudCheckService.class);

    private final ChatClient chatClient;

    @Value("classpath:/prompts/fraud-check.st")
    private Resource fraudCheckPrompt;

    public FraudCheckService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Cacheable(value = "ai-fraud", key = "#request")
    @Retry(name = "ai")
    @CircuitBreaker(name = "ai")
    public FraudCheckResponse checkFraud(FraudCheckRequest request) {
        log.info("Running fraud check for orderId={}", request.orderId());

        PromptTemplate promptTemplate = new PromptTemplate(this.fraudCheckPrompt);
        Prompt prompt = promptTemplate.create(
                java.util.Map.of(
                        "orderId", request.orderId(),
                        "userId", request.userId(),
                        "amount", request.amount(),
                        "currency", request.currency(),
                        "paymentMethod", request.paymentMethod(),
                        "userCountry", request.userCountry(),
                        "merchantCountry", request.merchantCountry(),
                        "userOrderCount", request.userOrderCount()
                )
        );

        return this.chatClient.prompt(prompt)
                .call()
                .entity(FraudCheckResponse.class);
    }
}
