package com.finance.order.service;

import com.finance.order.domain.Order;
import com.finance.order.domain.OrderRepository;
import com.finance.shared.event.InventoryReservedEvent;
import com.finance.shared.event.OrderItem;
import com.finance.shared.event.OrderPlacedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
import com.finance.shared.event.PaymentStatus;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String orderPlacedTopic;

    public OrderService(OrderRepository orderRepository,
                        KafkaTemplate<String, Object> kafkaTemplate,
                        @org.springframework.beans.factory.annotation.Value("${app.kafka.topic.order-placed:orders.placed}") String orderPlacedTopic) {
        this.orderRepository = orderRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.orderPlacedTopic = orderPlacedTopic;
    }

    @CachePut(value = "orders", key = "#result.id")
    public Order createOrder(UUID userId, List<OrderItem> items, BigDecimal totalAmount) {
        Order order = new Order(userId, items, totalAmount, "PLACED");
        order = orderRepository.save(order);

        OrderPlacedEvent event = new OrderPlacedEvent(order.getId(), order.getUserId(), order.getItems(), order.getTotalAmount());
        kafkaTemplate.send(orderPlacedTopic, order.getId().toString(), event);
        return order;
    }

    @Cacheable(value = "orders", key = "#id")
    public Optional<Order> getOrder(UUID id) {
        return orderRepository.findById(id);
    }

    public List<Order> getOrdersByUser(UUID userId) {
        return orderRepository.findByUserId(userId);
    }

    @CacheEvict(value = "orders", key = "#event.orderId()")
    public void processPayment(PaymentProcessedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> {
            if (event.status() == PaymentStatus.SUCCESS) {
                order.setPaymentCompleted(true);
                order.setStatus(order.isInventoryReserved() ? "COMPLETED" : "PAYMENT_COMPLETED");
            } else {
                order.setStatus("FAILED");
            }
            orderRepository.save(order);
        });
    }

    @CacheEvict(value = "orders", key = "#event.orderId()")
    public void processInventory(InventoryReservedEvent event) {
        orderRepository.findById(event.orderId()).ifPresent(order -> {
            if (event.status() == com.finance.shared.event.InventoryStatus.RESERVED) {
                order.setInventoryReserved(true);
                order.setStatus(order.isPaymentCompleted() ? "COMPLETED" : "INVENTORY_RESERVED");
            } else {
                order.setStatus("FAILED");
            }
            orderRepository.save(order);
        });
    }
}
