package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private Long userId;
    private int numberProduct;
    private List<CartLineItemResponse> cartLineItems;
    private double total;
}
