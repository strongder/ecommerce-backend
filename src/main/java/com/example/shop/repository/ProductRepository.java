package com.example.shop.repository;


import com.example.shop.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long>{

	Page<Product> findByCategoryId(Long CategoryId, Pageable pageable);
	Product findByVarProductsId(Long varProductId);

	@Query(value = "SELECT * FROM product WHERE MATCH(name, description) AGAINST (:key IN BOOLEAN MODE)", nativeQuery = true)
	Page<Product> searchByKeyword(@Param("key") String key, Pageable pageable);

	@Query(value = "SELECT * FROM product WHERE discount > 0", nativeQuery = true)
	Page<Product> findByDiscount(Pageable pageable);

}
