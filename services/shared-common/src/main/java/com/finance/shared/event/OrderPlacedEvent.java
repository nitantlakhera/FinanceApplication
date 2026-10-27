package com.finance.shared.event;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record OrderPlacedEvent(
        UUID eventId,
        UUID orderId,
        UUID userId,
        List<OrderItem> items,
        BigDecimal totalAmount,
        Instant occurredAt
) {
    public OrderPlacedEvent(UUID orderId, UUID userId, List<OrderItem> items, BigDecimal totalAmount) {
        this(UUID.randomUUID(), orderId, userId, items, totalAmount, Instant.now());
    }
}
