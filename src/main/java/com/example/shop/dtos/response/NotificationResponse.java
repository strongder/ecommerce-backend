package com.example.shop.dtos.response;

import com.example.shop.model.NotificationType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationResponse {

    private Long id;
    private String message;
    private NotificationType type;
    private String data;
    private String title;
    private String isDelivered ;
    private LocalDateTime createdAt;
}
