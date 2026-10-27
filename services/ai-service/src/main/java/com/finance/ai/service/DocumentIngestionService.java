package com.finance.ai.service;

import com.finance.ai.dto.IngestRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DocumentIngestionService {

    private static final Logger log = LoggerFactory.getLogger(DocumentIngestionService.class);

    private final VectorStore vectorStore;

    public DocumentIngestionService(org.springframework.beans.factory.ObjectProvider<VectorStore> vectorStoreProvider) {
        this.vectorStore = vectorStoreProvider.getIfAvailable();
    }

    public void ingest(IngestRequest request) {
        if (this.vectorStore == null) {
            throw new IllegalStateException("Vector store is not configured. Ensure PostgreSQL pgvector is available.");
        }
        log.info("Ingesting document id={}", request.documentId());

        Map<String, Object> metadata = new java.util.HashMap<>();
        metadata.put("documentId", request.documentId());
        if (request.metadata() != null && !request.metadata().isBlank()) {
            metadata.put("extra", request.metadata());
        }

        Document document = new Document(request.content(), metadata);
        this.vectorStore.add(List.of(document));
    }
}
