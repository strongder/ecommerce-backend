package com.example.shop.repository;


import com.example.shop.model.Product;
import jakarta.persistence.Tuple;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByCategoryId(Long CategoryId, Pageable pageable);

    Product findByVarProductsId(Long varProductId);

    @Query(value = "SELECT * FROM product WHERE MATCH(name, description) AGAINST (:key IN BOOLEAN MODE)", nativeQuery = true)
    Page<Product> searchByKeyword(@Param("key") String key, Pageable pageable);

    @Query(value = "SELECT * FROM product WHERE discount > 0", nativeQuery = true)
    Page<Product> findByDiscount(Pageable pageable);

    @Query(value = "SELECT p.name AS productName, " +
            "SUM(od.quantity) AS totalSold, " +
            "SUM(o.total) AS totalRevenue, " +
            "(SELECT ip.image_url FROM image_product ip WHERE ip.product_id = p.id LIMIT 1) AS productImage " +  // Get the first image
            "FROM order_item od " +
            "JOIN var_product vp ON od.var_product_id = vp.id " +
            "JOIN product p ON vp.product_id = p.id " +
            "JOIN category c ON p.category_id = c.id " +
            "JOIN `order` o ON od.order_id = o.id " +
            "WHERE (:categoryId IS NULL OR c.id = :categoryId) " +  // Conditional categoryId filter
            "AND o.created_at BETWEEN :startDate AND :endDate " +
            "AND o.status = 'COMPLETED' " +
            "GROUP BY p.name " +  // No need to group by image_url anymore
            "ORDER BY totalSold DESC",
            nativeQuery = true)
    List<Tuple> findTopSellingProductsByCategoryAndDate(
            @Param("categoryId") Long categoryId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    @Query(value = "SELECT p.name AS productName,\n" +
            "       COALESCE(SUM(od.quantity), 0) AS totalSold,\n" +
            "       COALESCE(SUM(o.total), 0) AS totalRevenue\n" +
            "FROM product p\n" +
            "         LEFT JOIN var_product vp ON p.id = vp.product_id\n" +
            "         LEFT JOIN order_item od ON vp.id = od.var_product_id\n" +
            "         LEFT JOIN `order` o ON od.order_id = o.id\n" +
            "AND o.created_at BETWEEN ?1 AND ?2\n" +
            "AND o.status = 'COMPLETED'\n" +
            "GROUP BY p.name\n" +
            "ORDER BY totalSold DESC;\n",
            nativeQuery = true)
    List<Tuple> productStatistics(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            Pageable pageable);


    @Query(value = "SELECT sum(oi.quantity) FROM `order` o JOIN order_item oi ON oi.order_id = o.id where o.status= 'COMPLETED'", nativeQuery = true)
    int totalProductSold();

    @Query(value = "SELECT SUM(p.stock) FROM Product p", nativeQuery = true)
    int totalProductInStock();
}
