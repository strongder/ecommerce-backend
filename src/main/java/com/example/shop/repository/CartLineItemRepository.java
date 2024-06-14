package com.example.shop.repository;

import com.example.shop.model.CartLineItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;

public interface CartLineItemRepository extends JpaRepository<CartLineItem, Long> {

	CartLineItem findByVariantProductId(Long id);

	@Query("Select count(c.id) from CartLineItem c where c.isDelete = false and c.cart.id = :id")
	int numberProduct(@Param("id") Long id);

	@Query("Select c from CartLineItem c where c.cart.id = :id and c.isDelete = false")
	Set<CartLineItem> findByCart(@Param("id") Long id);
}
