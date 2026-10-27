package com.finance.notification.messaging;

import com.finance.notification.service.NotificationService;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.OrderPlacedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class NotificationEventListener {

    private final NotificationService notificationService;

    public NotificationEventListener(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @KafkaListener(topics = "${app.kafka.topic.order-placed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onOrderPlaced(OrderPlacedEvent event) {
        notificationService.onOrderPlaced(event);
    }

    @KafkaListener(topics = "${app.kafka.topic.payment-processed}", groupId = "${spring.kafka.consumer.group-id}")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        notificationService.onPaymentProcessed(event);
    }

    @KafkaListener(topics = "${app.kafka.topic.inventory-reserved}", groupId = "${spring.kafka.consumer.group-id}")
    public void onInventoryReserved(InventoryReservedEvent event) {
        notificationService.onInventoryReserved(event);
    }
}
