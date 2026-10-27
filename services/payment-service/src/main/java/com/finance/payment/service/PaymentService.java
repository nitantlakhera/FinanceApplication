package com.finance.payment.service;

import com.finance.payment.domain.Payment;
import com.finance.payment.domain.PaymentRepository;
import com.finance.shared.event.OrderPlacedEvent;
import com.finance.shared.event.PaymentProcessedEvent;
import com.finance.shared.event.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, Object> kafkaTemplate;
    private final String paymentProcessedTopic;
    private final BigDecimal maxAmount = BigDecimal.valueOf(1000);

    public PaymentService(PaymentRepository paymentRepository,
                          KafkaTemplate<String, Object> kafkaTemplate,
                          @Value("${app.kafka.topic.payment-processed:payments.processed}") String paymentProcessedTopic) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentProcessedTopic = paymentProcessedTopic;
    }

    @CacheEvict(value = "payments", key = "#event.orderId()")
    public void processOrderPlaced(OrderPlacedEvent event) {
        PaymentStatus status = event.totalAmount().compareTo(maxAmount) <= 0
                ? PaymentStatus.SUCCESS
                : PaymentStatus.FAILED;
        String reference = "TXN-" + UUID.randomUUID();

        Payment payment = new Payment(event.orderId(), event.totalAmount(), status.name(), reference);
        paymentRepository.save(payment);

        PaymentProcessedEvent processedEvent = new PaymentProcessedEvent(
                event.orderId(), status, event.totalAmount(), reference);
        kafkaTemplate.send(paymentProcessedTopic, event.orderId().toString(), processedEvent);
    }

    public List<Payment> listPayments() {
        return paymentRepository.findAll();
    }

    @Cacheable(value = "payments", key = "#orderId")
    public Optional<Payment> findByOrderId(UUID orderId) {
        return paymentRepository.findByOrderId(orderId);
    }
}
