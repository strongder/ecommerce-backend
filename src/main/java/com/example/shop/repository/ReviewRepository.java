package com.example.shop.repository;

import com.example.shop.model.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    Optional<Page<Review>> findByProductId(Long productId, Pageable pageable);
    Optional<List<Review>> findByProductId(Long productId);
}
