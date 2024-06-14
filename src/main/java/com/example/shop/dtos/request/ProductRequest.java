package com.example.shop.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ProductRequest {

//    private Long id;
    private String code;
    private String name ;
    private String image;
    private Long categoryId;
    private String description;
    private Double price;
}
