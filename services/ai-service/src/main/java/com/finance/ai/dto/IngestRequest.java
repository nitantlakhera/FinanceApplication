package com.finance.ai.dto;

public record IngestRequest(
        String documentId,
        String content,
        String metadata
) {
}
