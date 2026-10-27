package com.finance.inventory.service;

import com.finance.inventory.domain.InventoryItem;
import com.finance.inventory.domain.InventoryRepository;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.OrderItem;
import com.finance.shared.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void processOrderPlaced_reservesStockAndPublishesEvent() {
        UUID orderId = UUID.randomUUID();
        InventoryItem item = new InventoryItem("PROD-1", 10);

        when(inventoryRepository.findById("PROD-1")).thenReturn(Optional.of(item));
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(i -> i.getArgument(0));

        OrderPlacedEvent event = new OrderPlacedEvent(
                orderId, UUID.randomUUID(), List.of(new OrderItem("PROD-1", 2)), BigDecimal.valueOf(20.00));

        InventoryService service = new InventoryService(inventoryRepository, kafkaTemplate, "inventory.reserved");
        service.processOrderPlaced(event);

        assertThat(item.getAvailableQuantity()).isEqualTo(8);
        assertThat(item.getReservedQuantity()).isEqualTo(2);

        ArgumentCaptor<InventoryReservedEvent> eventCaptor = ArgumentCaptor.forClass(InventoryReservedEvent.class);
        verify(kafkaTemplate).send(any(), any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().status().name()).isEqualTo("RESERVED");
    }

    @Test
    void addStock_increasesAvailableQuantity() {
        when(inventoryRepository.findById("PROD-2")).thenReturn(Optional.empty());
        when(inventoryRepository.save(any(InventoryItem.class))).thenAnswer(i -> i.getArgument(0));

        InventoryService service = new InventoryService(inventoryRepository, kafkaTemplate, "inventory.reserved");
        InventoryItem result = service.addStock("PROD-2", 50);

        assertThat(result.getAvailableQuantity()).isEqualTo(50);
    }
}
