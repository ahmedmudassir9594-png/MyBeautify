package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.Order;
import com.cosmetics.ecommerce.enums.OrderStatus;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.*;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUserIdOrderByCreatedAtDesc(Long userId, Pageable pageable);
    Optional<Order> findByIdAndUserId(Long id, Long userId);
    Optional<Order> findByOrderNumberAndUserId(String orderNumber, Long userId);
    long countByUserId(Long userId);
    long countByOrderStatus(OrderStatus status);
    long countByCreatedAtGreaterThanEqual(Instant from);
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.paymentStatus = com.cosmetics.ecommerce.enums.PaymentStatus.SUCCESS")
    BigDecimal totalRevenue();
    @org.springframework.data.jpa.repository.Query("select coalesce(sum(o.totalAmount), 0) from Order o where o.paymentStatus = com.cosmetics.ecommerce.enums.PaymentStatus.SUCCESS and o.createdAt >= :from")
    BigDecimal revenueSince(Instant from);
}