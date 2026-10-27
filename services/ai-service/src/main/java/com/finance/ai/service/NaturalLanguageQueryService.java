package com.finance.ai.service;

import com.finance.ai.dto.ChatRequest;
import com.finance.ai.dto.ChatResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import org.slf4j.Logger;
import org.springframework.cache.annotation.Cacheable;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class NaturalLanguageQueryService {

    private static final Logger log = LoggerFactory.getLogger(NaturalLanguageQueryService.class);

    private final ChatClient chatClient;
    private final VectorStore vectorStore;
    private final AiModelRouter modelRouter;

    @Value("classpath:/prompts/nl-query.st")
    private Resource nlQueryPrompt;

    public NaturalLanguageQueryService(ChatClient chatClient,
            org.springframework.beans.factory.ObjectProvider<VectorStore> vectorStoreProvider,
            AiModelRouter modelRouter) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStoreProvider.getIfAvailable();
        this.modelRouter = modelRouter;
    }

    @Cacheable(value = "ai-ask", key = "#request.question()")
    @Retry(name = "ai")
    @CircuitBreaker(name = "ai")
    public ChatResponse ask(ChatRequest request) {
        log.info("Processing natural language question");

        String context = "No vector store configured.";
        if (this.vectorStore != null) {
            SearchRequest searchRequest = SearchRequest.builder().query(request.question()).topK(5).build();
            context = this.vectorStore.similaritySearch(searchRequest).stream()
                    .map(document -> document.getText())
                    .collect(java.util.stream.Collectors.joining("\n---\n"));
        }

        if (context.isBlank()) {
            context = "No relevant documents found in the knowledge base.";
        }

        PromptTemplate promptTemplate = new PromptTemplate(this.nlQueryPrompt);
        Prompt prompt = promptTemplate.create(
                java.util.Map.of("question", request.question(), "context", context)
        );

        String answer = this.chatClient.prompt(prompt)
                .call()
                .content();

        return new ChatResponse(answer, this.modelRouter.getProvider());
    }
}
