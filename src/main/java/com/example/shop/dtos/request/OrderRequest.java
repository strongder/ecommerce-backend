package com.example.shop.dtos.request;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class OrderRequest {
	private Long userId;
	private Long addressId;
	private Date receiveTime;
	private Date deliveryTime;
	private String status;
}
