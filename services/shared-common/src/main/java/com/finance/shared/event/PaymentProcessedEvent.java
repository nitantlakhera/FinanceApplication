package com.finance.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentProcessedEvent(
        UUID eventId,
        UUID orderId,
        PaymentStatus status,
        BigDecimal amount,
        String transactionReference,
        Instant occurredAt
) {
    public PaymentProcessedEvent(UUID orderId, PaymentStatus status, BigDecimal amount, String transactionReference) {
        this(UUID.randomUUID(), orderId, status, amount, transactionReference, Instant.now());
    }
}
