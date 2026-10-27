package com.finance.payment.service;

import com.finance.payment.domain.Payment;
import com.finance.payment.domain.PaymentRepository;
import com.finance.shared.event.OrderItem;
import com.finance.shared.event.OrderPlacedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
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
class PaymentServiceTest {

    @Mock
    private PaymentRepository paymentRepository;

    @Mock
    private KafkaTemplate<String, Object> kafkaTemplate;

    @Test
    void processOrderPlaced_successForSmallAmount() {
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderPlacedEvent event = new OrderPlacedEvent(
                orderId, userId, List.of(new OrderItem("PROD-1", 1)), BigDecimal.valueOf(99.00));

        when(paymentRepository.save(any(Payment.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PaymentService service = new PaymentService(paymentRepository, kafkaTemplate, "payments.processed");
        service.processOrderPlaced(event);

        ArgumentCaptor<Payment> paymentCaptor = ArgumentCaptor.forClass(Payment.class);
        verify(paymentRepository).save(paymentCaptor.capture());
        assertThat(paymentCaptor.getValue().getStatus()).isEqualTo("SUCCESS");

        ArgumentCaptor<PaymentProcessedEvent> eventCaptor = ArgumentCaptor.forClass(PaymentProcessedEvent.class);
        verify(kafkaTemplate).send(any(), any(), eventCaptor.capture());
        assertThat(eventCaptor.getValue().status().name()).isEqualTo("SUCCESS");
    }
}
