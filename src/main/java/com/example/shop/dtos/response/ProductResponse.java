package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;


@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name ;
    private Integer stock;
    private Integer quantitySold;
    private Float rating;
    private List<ImageProductResponse> imageUrls;
    private String description;
    private int discount;
    private Double price;
    private List<VarProductResponse> varProducts;
    private Long categoryId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDelete;
}
