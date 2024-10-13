package com.example.shop.model;


import com.example.shop.dtos.NotificationType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    private NotificationType type;

    private String title;

    private String message;

    @Column(name = "is_read", columnDefinition = "boolean default false")
    private boolean isRead;

    @Column (name = "data", columnDefinition = "TEXT")
    private String data;

    private LocalDateTime createdAt;

    @ManyToOne
    private User user;

}
