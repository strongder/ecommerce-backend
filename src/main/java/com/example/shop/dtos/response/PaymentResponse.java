package com.example.shop.dtos.response;


import com.example.shop.dtos.PaymentStatusType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponse {
    private String transactionId; // Mã giao dịch được hệ thống hoặc VNPay sinh ra
    private String orderCode; // ID của đơn hàng liên quan
    private Double amount; // Số tiền đã thanh toán
    private String paymentMethod; // Phương thức thanh toán (VNPay, PayPal, ...)
    private PaymentStatusType status; // Trạng thái thanh toán (success, failed, pending)
    private LocalDateTime paymentTime;
}
