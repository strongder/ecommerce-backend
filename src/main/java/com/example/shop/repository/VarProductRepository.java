package com.example.shop.repository;

import com.example.shop.model.VarProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VarProductRepository extends JpaRepository<VarProduct, Long> {
    List<VarProduct> findByProductId(Long productId);
}
