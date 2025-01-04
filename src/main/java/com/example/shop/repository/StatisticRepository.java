package com.example.shop.repository;

import com.example.shop.model.Order;
import jakarta.persistence.Tuple;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface StatisticRepository extends JpaRepository<Order, Long> {


    @Query(value = "Select c.name, sum(p.quantity_sold) as quantity from category c join product p on c.id = p.category_id where c.parent_id is not null group by c.name", nativeQuery = true)
    List<Tuple> getStatisticCategoyByProduct();

    @Query(value = "Select c.name, sum(p.quantity_sold * p.price) as total from category c join product p on c.id = p.category_id where c.parent_id is not null group by c.name", nativeQuery = true)
    List<Tuple> getStatisticProductByTotal();

    @Query(value = "SELECT EXTRACT(MONTH FROM o.created_at) AS month, " +
            "       SUM(o.total) AS total, " +
            "       SUM(oi.quantity) AS quantitySold, " +
            "       COUNT(o.id) AS totalOrder " +
            "FROM `order` o " +
            "JOIN order_item oi ON o.id = oi.order_id " +
            "WHERE EXTRACT(YEAR FROM o.created_at) = :year AND o.status = 'COMPLETED' " +
            "GROUP BY month " +
            "ORDER BY month", nativeQuery = true)
    List<Tuple> getStatisticRevenueByMonth(@Param("year") int year);

    // query lay ra tong doanh thu, tong so don hang, so don hang thanh cong, so don hang that bai, so don hang dang xu ly, so don hang dang ship
    @Query(value = "SELECT " +
            "       COUNT(o.id) AS totalOrder, " +
            "       SUM(CASE WHEN o.status = 'COMPLETED' THEN o.total ELSE 0 END) AS totalRevenue, " +
            "       COUNT(CASE WHEN o.status = 'COMPLETED' THEN o.id END) AS totalCompletedOrder, " +
            "       COUNT(CASE WHEN o.status = 'PENDING' OR o.status = 'PENDING_PAYMENT' THEN o.id END) AS totalPendingOrder, " +
            "       COUNT(CASE WHEN o.status = 'CANCELLED' THEN o.id END) AS totalFailedOrder, " +
            "       COUNT(CASE WHEN o.status = 'PROCESSING' THEN o.id END) AS totalProcessingOrder, " +
            "       COUNT(CASE WHEN o.status = 'SHIPPED' THEN o.id END) AS totalShippingOrder, " +
            "       COUNT(CASE WHEN o.payment_method = 'COD' THEN o.id END) AS totalCodPayment, " +
            "       COUNT(CASE WHEN o.payment_method = 'VNPAY' THEN o.id END) AS totalVnpayPayment " +
            "FROM `order` o " +
            "WHERE o.created_at BETWEEN :startDate AND :endDate", nativeQuery = true)
    Tuple getStatisticOrderOverview(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    // query lay ra tong so khach hang, so khach hang moi, so khach hang quay lai
    @Query(value = "SELECT\n" +
            "    COUNT(DISTINCT u.id) AS totalCustomers,  -- Tổng số khách hàng\n" +
            "    COUNT(DISTINCT CASE WHEN u.created_at >= NOW() - INTERVAL 30 DAY THEN u.id END) AS newCustomers,  \n" +
            "    COUNT(DISTINCT CASE WHEN o.id IS NOT NULL THEN u.id END) AS returnCustomers  \n" +
            "FROM\n" +
            "    user u\n" +
            "        JOIN\n" +
            "    user_roles ur ON u.id = ur.users_id\n" +
            "        JOIN\n" +
            "    role r ON ur.roles_id = r.id\n" +
            "        LEFT JOIN\n" +
            "    `order` o ON u.id = o.user_id\n" +
            "WHERE\n" +
            "    r.name = 'user';", nativeQuery = true)
    Tuple getStatisticCustomer();


}