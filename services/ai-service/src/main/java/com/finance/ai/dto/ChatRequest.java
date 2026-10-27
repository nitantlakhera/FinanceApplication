package com.finance.ai.dto;

public record ChatRequest(
        String question,
        String context
) {
}
