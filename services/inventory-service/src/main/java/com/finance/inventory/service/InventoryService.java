package com.finance.inventory.service;

import com.finance.inventory.domain.InventoryItem;
import com.finance.inventory.domain.InventoryRepository;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.InventoryStatus;
import com.finance.shared.event.OrderItem;
import com.finance.shared.event.OrderPlacedEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String inventoryReservedTopic;

    public InventoryService(InventoryRepository inventoryRepository,
                            KafkaTemplate<String, Object> kafkaTemplate,
                            @Value("${app.kafka.topic.inventory-reserved:inventory.reserved}") String inventoryReservedTopic) {
        this.inventoryRepository = inventoryRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.inventoryReservedTopic = inventoryReservedTopic;
    }

    @CacheEvict(value = "inventory", allEntries = true)
    public void processOrderPlaced(OrderPlacedEvent event) {
        List<OrderItem> items = event.items();
        if (items == null || items.isEmpty()) {
            publish(event.orderId(), null, 0, InventoryStatus.FAILED);
            return;
        }

        boolean allReserved = true;
        for (OrderItem item : items) {
            Optional<InventoryItem> optional = inventoryRepository.findById(item.productId());
            if (optional.isEmpty() || !optional.get().reserve(item.quantity())) {
                allReserved = false;
                break;
            }
        }

        if (allReserved) {
            for (OrderItem item : items) {
                inventoryRepository.save(inventoryRepository.findById(item.productId()).orElseThrow());
                publish(event.orderId(), item.productId(), item.quantity(), InventoryStatus.RESERVED);
            }
        } else {
            publish(event.orderId(), items.get(0).productId(), 0, InventoryStatus.FAILED);
        }
    }

    @CachePut(value = "inventory", key = "#productId")
    public InventoryItem addStock(String productId, int quantity) {
        InventoryItem item = inventoryRepository.findById(productId).orElse(new InventoryItem(productId, 0));
        item.setAvailableQuantity(item.getAvailableQuantity() + quantity);
        return inventoryRepository.save(item);
    }

    public List<InventoryItem> listInventory() {
        return inventoryRepository.findAll();
    }

    @Cacheable(value = "inventory", key = "#productId")
    public Optional<InventoryItem> getItem(String productId) {
        return inventoryRepository.findById(productId);
    }

    private void publish(java.util.UUID orderId, String productId, int quantity, InventoryStatus status) {
        InventoryReservedEvent event = new InventoryReservedEvent(orderId, productId, quantity, status);
        kafkaTemplate.send(inventoryReservedTopic, orderId.toString(), event);
    }
}
