package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReviewResponse {
    private Long id;
    private String comment;
    private int rating;
    private LocalDateTime createdAt;
    private UserResponse user;
    private ProductResponse product;
}

