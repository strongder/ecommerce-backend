package com.example.shop.repository;

import com.example.shop.dtos.PaymentStatusType;
import com.example.shop.model.Order;
import com.example.shop.model.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Long> {

    Optional<Payment> findByTransactionId(String transactionId);
    List<Payment> findByStatus(PaymentStatusType status);

    @Query("SELECT p FROM Payment p WHERE p.order.id IN (SELECT o.id FROM Order o WHERE o.user.id = :userId)")
    List<Payment> findByUserId(@Param("userId") Long userId);

    List<Payment> findByOrderIn(List<Order> orders);
}
