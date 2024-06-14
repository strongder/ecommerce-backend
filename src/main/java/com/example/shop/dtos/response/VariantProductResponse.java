package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class VariantProductResponse {

    private Long id;
    private String code;
    private String name ;
    private int amount;
    private String image;
    private String color;
    private Double price;
    private String size;
    private ProductResponse product;

}
