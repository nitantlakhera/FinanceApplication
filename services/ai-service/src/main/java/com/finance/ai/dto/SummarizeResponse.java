package com.finance.ai.dto;

public record SummarizeResponse(
        String summary,
        String status,
        String provider
) {
}
