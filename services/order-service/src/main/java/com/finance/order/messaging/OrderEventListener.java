package com.finance.order.messaging;

import com.finance.order.service.OrderService;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class OrderEventListener {

    private final OrderService orderService;

    public OrderEventListener(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "${app.kafka.topic.payment-processed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        orderService.processPayment(event);
    }

    @KafkaListener(topics = "${app.kafka.topic.inventory-reserved}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInventoryReserved(InventoryReservedEvent event) {
        orderService.processInventory(event);
    }
}
