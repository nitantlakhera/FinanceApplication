package com.finance.ai.dto;

public record FraudCheckResponse(
        String riskLevel,
        int riskScore,
        String explanation,
        boolean recommendedBlock
) {
}
