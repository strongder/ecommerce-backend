package com.example.shop.repository;

import com.example.shop.model.VariantProduct;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface VariantProductRepository extends JpaRepository<VariantProduct, Long>{

	List<VariantProduct> findByProductId(Long productId);
	Optional<VariantProduct> findByCode(String key);

}
