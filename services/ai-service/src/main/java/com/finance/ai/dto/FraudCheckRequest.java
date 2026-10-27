package com.finance.ai.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record FraudCheckRequest(
        UUID orderId,
        UUID userId,
        BigDecimal amount,
        String currency,
        String paymentMethod,
        String userCountry,
        String merchantCountry,
        int userOrderCount
) {
}
