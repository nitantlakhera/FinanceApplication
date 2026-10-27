package com.finance.ai.dto;

import java.util.UUID;

public record SummarizeRequest(
        UUID orderId,
        String orderStatus,
        String paymentStatus,
        String inventoryStatus
) {
}
