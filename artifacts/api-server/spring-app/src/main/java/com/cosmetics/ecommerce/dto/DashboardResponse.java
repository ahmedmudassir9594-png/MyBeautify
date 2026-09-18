package com.cosmetics.ecommerce.dto;

import java.math.BigDecimal;

public record DashboardResponse(long totalUsers, long totalCustomers, long totalProducts, long totalOrders,
                                long pendingOrders, long completedOrders, long cancelledOrders, BigDecimal totalRevenue,
                                long todaysOrders, BigDecimal todaysRevenue, long lowStockProducts) {}