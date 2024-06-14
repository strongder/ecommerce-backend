package com.example.shop.dtos.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AddressRequest {

    private Long id;
    private String street;
    private String city;
    private Long userId;
    private String zipCode;
}
