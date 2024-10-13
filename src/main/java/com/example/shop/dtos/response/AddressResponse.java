package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class AddressResponse {
    private Long id;
    private String phone;
    private String recipientName;
    private String city;
    private String district;
    private String ward;
    private boolean defaultAddress;
    private String addressDetail;
}
