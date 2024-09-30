package com.example.shop.service;


import com.example.shop.dtos.response.NotificationResponse;
import com.example.shop.model.Notification;
import com.example.shop.model.NotificationType;
import com.example.shop.model.User;
import com.example.shop.repository.NotificationRepository;
import com.example.shop.repository.UserRepository;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.messaging.simp.SimpMessagingTemplate;
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


    @Transactional
    public void sendOrderNotification(Long orderId, String title, String message) {
        String destination = "/topic/admin/notification";
        List<User> users = userRepository.findByRoleName("ADMIN");
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setTitle(title);
            notification.setType(NotificationType.ORDER);
            notification.setMessage(message);
            notification.setData("orderId:"+ orderId);
            notification.setDelivered(true);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            NotificationResponse response  = modelMapper.map(notification, NotificationResponse.class);
            messagingTemplate.convertAndSend(destination, response);
        }
    }

    @Transactional
    public void sendPaymentNotification(Long paymentId, String title, String message) {
        String destination = "/topic/admin/notification";
        List<User> users = userRepository.findByRoleName("ADMIN");
        for (User user : users) {
            Notification notification = new Notification();
            notification.setUser(user);
            notification.setType(NotificationType.PAYMENT);
            notification.setData("paymentId:"+ paymentId);
            notification.setTitle(title);
            notification.setMessage(message);
            notification.setDelivered(true);
            notification.setCreatedAt(LocalDateTime.now());
            notificationRepository.save(notification);
            messagingTemplate.convertAndSend(destination, notification);
        }
    }

    public void sendNotificationToUser(User user,String title, String message) {
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setType(NotificationType.ORDER);
        notification.setMessage(message);
        notification.setDelivered(true);
        notification.setCreatedAt(LocalDateTime.now());
        notificationRepository.save(notification);
        messagingTemplate.convertAndSendToUser(String.valueOf(user.getId()), "/queue/notification/" + user.getId(), notification);
    }

    public List<NotificationResponse> getNotificationsByUser(User user) {
        List<Notification> notifications = notificationRepository.findByUser(user);
        return notifications.stream().map(notification -> modelMapper.map(notification, NotificationResponse.class)).toList();
    }


}
