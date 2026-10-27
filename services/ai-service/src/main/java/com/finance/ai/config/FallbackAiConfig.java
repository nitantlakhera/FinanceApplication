package com.finance.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingRequest;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Configuration
public class FallbackAiConfig {

    private static final Logger log = LoggerFactory.getLogger(FallbackAiConfig.class);

    @Bean
    @ConditionalOnMissingBean(ChatClient.class)
    public ChatClient fallbackChatClient() {
        log.warn("No AI chat client configured; using fallback chat client that returns canned responses.");
        return ChatClient.builder(new FallbackChatModel()).build();
    }

    @Bean
    @ConditionalOnMissingBean(EmbeddingModel.class)
    public EmbeddingModel fallbackEmbeddingModel() {
        log.warn("No embedding model configured; using fallback embedding model with random vectors.");
        return new FallbackEmbeddingModel();
    }

    static class FallbackChatModel implements ChatModel {

        @Override
        public ChatResponse call(Prompt prompt) {
            String text = "{\"riskLevel\":\"LOW\",\"riskScore\":10,\"explanation\":\"Fallback response: no AI provider configured.\",\"recommendedBlock\":false}";
            if (!prompt.getInstructions().isEmpty()) {
                Message first = prompt.getInstructions().get(0);
                if (first.getText().toLowerCase().contains("summarize")) {
                    text = "Fallback summary: order is being processed.";
                } else if (first.getText().toLowerCase().contains("question") || first.getText().toLowerCase().contains("context")) {
                    text = "Fallback answer: please configure an AI provider (KIMI_API_KEY or Ollama).";
                }
            }
            return new ChatResponse(List.of(new Generation(new AssistantMessage(text))));
        }

        @Override
        public Flux<ChatResponse> stream(Prompt prompt) {
            return Flux.just(call(prompt));
        }

        @Override
        public ChatOptions getDefaultOptions() {
            return null;
        }
    }

    static class FallbackEmbeddingModel implements EmbeddingModel {

        private static final int DIMENSIONS = 1536;

        @Override
        public EmbeddingResponse call(EmbeddingRequest request) {
            List<org.springframework.ai.embedding.Embedding> embeddings = request.getInstructions().stream()
                    .map(text -> {
                        float[] vector = new float[DIMENSIONS];
                        java.util.Arrays.fill(vector, 0.0f);
                        return new org.springframework.ai.embedding.Embedding(vector, 0);
                    })
                    .toList();
            return new EmbeddingResponse(embeddings);
        }

        @Override
        public float[] embed(org.springframework.ai.document.Document document) {
            float[] vector = new float[DIMENSIONS];
            java.util.Arrays.fill(vector, 0.0f);
            return vector;
        }
    }
}
