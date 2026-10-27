package com.finance.notification.service;

import com.finance.notification.domain.Notification;
import com.finance.notification.domain.NotificationRepository;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.OrderPlacedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @CacheEvict(value = "notifications", key = "#event.orderId()")
    public void onOrderPlaced(OrderPlacedEvent event) {
        save(event.orderId(), "OrderPlacedEvent",
                "Order " + event.orderId() + " placed for user " + event.userId());
    }

    @CacheEvict(value = "notifications", key = "#event.orderId()")
    public void onPaymentProcessed(PaymentProcessedEvent event) {
        save(event.orderId(), "PaymentProcessedEvent",
                "Payment for order " + event.orderId() + " status: " + event.status());
    }

    @CacheEvict(value = "notifications", key = "#event.orderId()")
    public void onInventoryReserved(InventoryReservedEvent event) {
        save(event.orderId(), "InventoryReservedEvent",
                "Inventory for order " + event.orderId() + " status: " + event.status());
    }

    private void save(UUID orderId, String eventType, String message) {
        notificationRepository.save(new Notification(orderId, eventType, message));
    }

    public List<Notification> listNotifications() {
        return notificationRepository.findAll();
    }

    @Cacheable(value = "notifications", key = "#orderId")
    public List<Notification> findByOrderId(UUID orderId) {
        return notificationRepository.findByOrderId(orderId);
    }
}
