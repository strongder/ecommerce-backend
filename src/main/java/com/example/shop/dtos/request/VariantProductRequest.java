package com.example.shop.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VariantProductRequest {

    private Long id;
    private String name;
    private String code;
    private Long productId;
    private String image;
    private int amount;
    private String color;
    private Double price;
    private String size;

}
