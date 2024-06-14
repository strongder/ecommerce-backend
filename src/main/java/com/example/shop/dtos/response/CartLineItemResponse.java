package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CartLineItemResponse {

    private Long id;
    private int amount;
    private Double price;
    private VariantProductResponse variantProduct;
    private boolean isDelete;
}
