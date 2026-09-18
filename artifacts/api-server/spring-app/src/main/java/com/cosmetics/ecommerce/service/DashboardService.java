package com.cosmetics.ecommerce.service;

import com.cosmetics.ecommerce.dto.DashboardResponse;
import com.cosmetics.ecommerce.enums.OrderStatus;
import com.cosmetics.ecommerce.repository.*;
import org.springframework.stereotype.Service;
import java.time.*;

@Service
public class DashboardService {
    private final UserRepository users; private final ProductRepository products; private final OrderRepository orders;
    public DashboardService(UserRepository users, ProductRepository products, OrderRepository orders) { this.users = users; this.products = products; this.orders = orders; }
    public DashboardResponse get() {
        Instant start = LocalDate.now(ZoneOffset.UTC).atStartOfDay().toInstant(ZoneOffset.UTC);
        return new DashboardResponse(users.count(), users.countByRole(com.cosmetics.ecommerce.enums.Role.CUSTOMER), products.count(),
                orders.count(), orders.countByOrderStatus(OrderStatus.PENDING), orders.countByOrderStatus(OrderStatus.DELIVERED),
                orders.countByOrderStatus(OrderStatus.CANCELLED), orders.totalRevenue(), orders.countByCreatedAtGreaterThanEqual(start),
                orders.revenueSince(start), products.findLowStock().size());
    }
}