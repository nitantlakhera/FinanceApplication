package com.finance.notification.service;

import com.finance.notification.domain.Notification;
import com.finance.notification.domain.NotificationRepository;
import com.finance.shared.event.OrderPlacedEvent;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Test
    void onOrderPlaced_savesNotification() {
        when(notificationRepository.save(any(Notification.class))).thenAnswer(i -> i.getArgument(0));

        NotificationService service = new NotificationService(notificationRepository);
        UUID orderId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();
        OrderPlacedEvent event = new OrderPlacedEvent(
                orderId, userId, List.of(), BigDecimal.ZERO);

        service.onOrderPlaced(event);

        ArgumentCaptor<Notification> captor = ArgumentCaptor.forClass(Notification.class);
        verify(notificationRepository).save(captor.capture());
        assertThat(captor.getValue().getEventType()).isEqualTo("OrderPlacedEvent");
        assertThat(captor.getValue().getOrderId()).isEqualTo(orderId);
    }
}
