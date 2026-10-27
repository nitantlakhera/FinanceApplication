package com.finance.shared.event;

import java.time.Instant;
import java.util.UUID;

public record InventoryReservedEvent(
        UUID eventId,
        UUID orderId,
        String productId,
        int quantityReserved,
        InventoryStatus status,
        Instant occurredAt
) {
    public InventoryReservedEvent(UUID orderId, String productId, int quantityReserved, InventoryStatus status) {
        this(UUID.randomUUID(), orderId, productId, quantityReserved, status, Instant.now());
    }
}
