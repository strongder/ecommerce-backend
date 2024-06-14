package com.example.shop.repository;


import com.example.shop.model.Category;
import com.example.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface ProductRepository extends JpaRepository<Product, Long>{

	Page<Product> findByCategory(Optional<Category> category, Pageable pageable);

    Optional<Product> findByCode(String code);
}
