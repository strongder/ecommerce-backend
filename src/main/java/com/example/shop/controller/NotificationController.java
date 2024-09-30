package com.example.shop.controller;

import com.example.shop.dtos.response.ApiResponse;
import com.example.shop.dtos.response.NotificationResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.User;
import com.example.shop.repository.UserRepository;
import com.example.shop.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User user = userRepository.findByEmail(username).orElseThrow(() -> new AppException(ErrorResponse.USER_NOT_EXISTED));
        List<NotificationResponse> notifications = notificationService.getNotificationsByUser(user);
        return  ApiResponse.<List<NotificationResponse>>builder()
                .message("Get notifications successfully")
                .result(notifications).build();
    }
}
