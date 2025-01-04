package com.example.shop.service;


import com.example.shop.dtos.response.NotificationResponse;
import com.example.shop.exception.AppException;
import com.example.shop.exception.ErrorResponse;
import com.example.shop.model.Notification;
import com.example.shop.dtos.NotificationType;
import com.example.shop.model.Order;
import com.example.shop.model.User;
import com.example.shop.repository.NotificationRepository;
import com.example.shop.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
public class NotificationService {

    SimpMessagingTemplate messagingTemplate;
    NotificationRepository notificationRepository;
    UserRepository userRepository;
    ModelMapper modelMapper;
    ObjectMapper objectMapper;


    @Transactional
    public void sendOrderNotification(Order order, String title, String message) {
        String destination = "/topic/admin/notification";
        List<User> users = userRepository.findByRoleName("ADMIN");
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setType(NotificationType.ORDER);
            notification.setMessage(message);
            notification.setData(String.valueOf(order.getId()));
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            NotificationResponse response  = modelMapper.map(notification, NotificationResponse.class);
            messagingTemplate.convertAndSend(destination, response);
        }
    }

    @Transactional
    public void sendPaymentNotification(Order order, Long paymentId, String title, String message) {
        String destination = "/topic/admin/notification";
        List<User> users = userRepository.findByRoleName("ADMIN");
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType(NotificationType.PAYMENT);
            notification.setData(String.valueOf(order.getId()));
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            NotificationResponse response  = modelMapper.map(notification, NotificationResponse.class);
            messagingTemplate.convertAndSend(destination, response);
        }
    }

    public void sendNotificationToUser(Order order, String title, String message) {
        Notification notification = new Notification();
        notification.setUser(order.getUser());
        notification.setTitle(title);
        notification.setType(NotificationType.ORDER);
        notification.setMessage(message);
        notification.setData(String.valueOf(order.getId()));
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
        NotificationResponse response  = modelMapper.map(notification, NotificationResponse.class);
        messagingTemplate.convertAndSendToUser(String.valueOf(order.getUser().getId()), "/queue/notify", response);
    }

    public List<NotificationResponse> getNotifications() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorResponse.USER_NOT_EXISTED));
        List<Notification> notifications = notificationRepository.findByUser(user).reversed();
        return notifications.stream().map(notification -> modelMapper.map(notification, NotificationResponse.class)).toList();
    }

    public void makeAsRead(Long id) {
        Notification notification = notificationRepository.findById(id).orElseThrow(() -> new AppException(ErrorResponse.NOTIFICATION_NOT_EXISTED));
        notification.setRead(true);
        notificationRepository.save(notification);
    }

    public Integer getUnreadNotificationCount() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorResponse.USER_NOT_EXISTED));
        return notificationRepository.countByUserAndIsReadFalse(user);
    }
}
