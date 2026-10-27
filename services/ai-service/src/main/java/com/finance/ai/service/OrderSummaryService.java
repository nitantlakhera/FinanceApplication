package com.finance.ai.service;

import com.finance.ai.dto.SummarizeRequest;
import com.finance.ai.dto.SummarizeResponse;
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
public class OrderSummaryService {

    private static final Logger log = LoggerFactory.getLogger(OrderSummaryService.class);

    private final ChatClient chatClient;
    private final AiModelRouter modelRouter;

    @Value("classpath:/prompts/summarize-order.st")
    private Resource summarizePrompt;

    public OrderSummaryService(ChatClient chatClient, AiModelRouter modelRouter) {
        this.chatClient = chatClient;
        this.modelRouter = modelRouter;
    }

    @Cacheable(value = "ai-summary", key = "#request")
    @Retry(name = "ai")
    @CircuitBreaker(name = "ai")
    public SummarizeResponse summarize(SummarizeRequest request) {
        log.info("Summarizing orderId={}", request.orderId());

        PromptTemplate promptTemplate = new PromptTemplate(this.summarizePrompt);
        Prompt prompt = promptTemplate.create(
                java.util.Map.of(
                        "orderId", request.orderId(),
                        "orderStatus", request.orderStatus(),
                        "paymentStatus", request.paymentStatus(),
                        "inventoryStatus", request.inventoryStatus()
                )
        );

        String summary = this.chatClient.prompt(prompt)
                .call()
                .content();

        return new SummarizeResponse(summary, "SUCCESS", this.modelRouter.getProvider());
    }
}
