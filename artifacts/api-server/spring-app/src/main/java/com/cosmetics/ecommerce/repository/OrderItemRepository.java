package com.cosmetics.ecommerce.repository;

import com.cosmetics.ecommerce.entity.OrderItem;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    @Query("select oi.product.id, sum(oi.quantity) from OrderItem oi group by oi.product.id order by sum(oi.quantity) desc")
    List<Object[]> topSellingProducts();
    boolean existsByProductIdAndOrderUserIdAndOrderOrderStatus(Long productId, Long userId, com.cosmetics.ecommerce.enums.OrderStatus status);
}