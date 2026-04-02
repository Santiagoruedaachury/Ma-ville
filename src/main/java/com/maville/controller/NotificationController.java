package com.maville.controller;

import com.maville.model.Notification;
import com.maville.model.User;
import com.maville.service.NotificationService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<Notification> getAll(@AuthenticationPrincipal User user) {
        return notificationService.getByUser(user.getId());
    }

    @GetMapping("/unread")
    public List<Notification> getUnread(@AuthenticationPrincipal User user) {
        return notificationService.getUnread(user.getId());
    }

    @PatchMapping("/{id}/read")
    public void markAsRead(@PathVariable Long id, @AuthenticationPrincipal User user) {
        notificationService.markAsRead(id, user.getId());
    }
}
