package com.smarttaskmanager.controller;

import com.smarttaskmanager.dto.NotificationDto;
import com.smarttaskmanager.service.NotificationService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@CrossOrigin(origins = { "http://localhost:5173", "http://localhost:3000" })
public class NotificationController {
    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public List<NotificationDto> list(Authentication auth) {
        return notificationService.list(auth.getName());
    }

    @GetMapping("/unread-count")
    public Map<String, Long> unreadCount(Authentication auth) {
        return Map.of("unread", notificationService.unreadCount(auth.getName()));
    }

    @PatchMapping("/{id}/read")
    public Map<String, String> markRead(Authentication auth, @PathVariable Long id) {
        notificationService.markRead(auth.getName(), id);
        return Map.of("status", "read");
    }
}
