package com.example.shop.repository;

import com.example.shop.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findByName(String name);
    List<Category> findByParentCategoryIsNullAndIsDeleteFalse();
    Page<Category> findByIsDeleteFalse(Pageable pageable);
}
