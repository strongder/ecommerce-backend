package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String code;
    private String name ;
    private String amount;
    private String image;
    private String description;
    private Double price;
    private CategoryResponse category;
    private boolean status;
    private LocalDateTime createdAt;
    private boolean isDelete;
}
