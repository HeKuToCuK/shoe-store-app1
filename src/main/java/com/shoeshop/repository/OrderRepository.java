package com.shoeshop.repository;

import com.shoeshop.model.Order;
import com.shoeshop.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface OrderRepository extends JpaRepository<Order, Long> {
    List<Order> findByCustomer(User customer);
    List<Order> findByOrderDateBetween(LocalDateTime start, LocalDateTime end);
    @Query("SELECT DATE_TRUNC('day', o.orderDate) AS date, SUM(o.totalPrice) AS total " +
            "FROM Order o GROUP BY DATE_TRUNC('day', o.orderDate)")
    Map<LocalDate, Double> findDailySalesGroupedByDate();
    @Query("SELECT SUM(o.totalPrice) FROM Order o WHERE o.orderDate BETWEEN :start AND :end")
    Double sumTotalByPeriod(@Param("start") LocalDateTime start,
                            @Param("end") LocalDateTime end);
    @Query("SELECT DISTINCT o FROM Order o LEFT JOIN FETCH o.orderDetails WHERE o.orderDate BETWEEN :start AND :end")
    List<Order> findByOrderDateBetweenWithDetails(@Param("start") LocalDateTime start,
                                                  @Param("end") LocalDateTime end);
    @Query("SELECT o FROM Order o " +
            "LEFT JOIN FETCH o.orderDetails od " +
            "LEFT JOIN FETCH od.product " +
            "WHERE o.id = :id")
    Optional<Order> findByIdWithDetails(@Param("id") Long id);
}