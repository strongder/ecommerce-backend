package com.example.shop.model;

import com.example.shop.dtos.PaymentStatusType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "payment_transaction")
@Builder
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "transaction_id")
    private String transactionId; // mã giao dịch

    @Column(name = "amount")
    private Double amount; // giá trị thanh toán

    @Column(name = "payment_method")
    private String paymentMethod; // vnpay, paypal, ...

    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private PaymentStatusType status; // pending, success, failed

    private LocalDateTime paymentTime;

    @ManyToOne
    private Order order;

}
