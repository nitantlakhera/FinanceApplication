package com.finance.order.service;

import com.finance.order.domain.Order;
import com.finance.order.domain.OrderRepository;
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
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void createOrder_savesAndPublishesEvent() {
        UUID userId = UUID.randomUUID();
        List<OrderItem> items = List.of(new OrderItem("PROD-1", 2));
        BigDecimal total = BigDecimal.valueOf(49.99);

        Order order = new Order(userId, items, total, "PLACED");
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        OrderService orderService = new OrderService(orderRepository, kafkaTemplate, "orders.placed");
        Order result = orderService.createOrder(userId, items, total);

        assertThat(result.getStatus()).isEqualTo("PLACED");
        verify(orderRepository).save(any(Order.class));

        ArgumentCaptor<String> topicCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<String> keyCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<OrderPlacedEvent> eventCaptor = ArgumentCaptor.forClass(OrderPlacedEvent.class);
        verify(kafkaTemplate).send(topicCaptor.capture(), keyCaptor.capture(), eventCaptor.capture());

        assertThat(topicCaptor.getValue()).isEqualTo("orders.placed");
        assertThat(eventCaptor.getValue().orderId()).isEqualTo(order.getId());
    }
}
