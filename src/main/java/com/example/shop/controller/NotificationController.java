package com.example.shop.controller;

import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.NotificationResponse;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/notifications")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class NotificationController {

    NotificationService notificationService;
    UserRepository userRepository;

    @GetMapping
    public ApiResponse<List<NotificationResponse>> getNotifications() {
        List<NotificationResponse> notifications = notificationService.getNotifications();
        return  ApiResponse.<List<NotificationResponse>>builder()
                .message("Get notifications successfully")
                .result(notifications).build();
    }

    @PutMapping("/read/{id}")
    public ApiResponse<?> readNotification(@PathVariable("id") Long id) {
        notificationService.makeAsRead(id);
        return ApiResponse.builder()
                .message("Notification is read")
                .build();
    }

    @GetMapping("/unread-count")
    public ApiResponse<Integer> getUnreadNotificationCount() {
        int count = notificationService.getUnreadNotificationCount();
        return ApiResponse.<Integer>builder()
                .message("Get unread notification count successfully")
                .result(count)
                .build();
    }
}
