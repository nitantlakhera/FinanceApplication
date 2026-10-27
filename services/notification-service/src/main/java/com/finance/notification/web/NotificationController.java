package com.finance.notification.web;

import com.finance.notification.domain.Notification;
import com.finance.notification.service.NotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<Notification>> listNotifications() {
        return ResponseEntity.ok(notificationService.listNotifications());
    }

    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Notification>> getByOrder(@PathVariable UUID orderId) {
        return ResponseEntity.ok(notificationService.findByOrderId(orderId));
    }
}
