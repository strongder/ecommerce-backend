package com.example.shop.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.Set;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderResponse {
	private LocalDateTime createdAt;
	private Date deliveryTime;
	private Date receiveTime;
	private Double total;
	private String status;
	private String recipientName;
	private AddressResponse address;
	private Set<CartLineItemResponse> cartLineItems;
}