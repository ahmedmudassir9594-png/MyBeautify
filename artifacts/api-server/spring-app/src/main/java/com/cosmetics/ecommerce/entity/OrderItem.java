package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "order_items")
public class OrderItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Order order;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @Column(nullable = false) private String productName;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal subtotal;
    protected OrderItem() {}
    public OrderItem(Order order, Product product, int quantity, BigDecimal price) {
        this.order = order; this.product = product; this.productName = product.getName(); this.quantity = quantity;
        this.price = price; this.subtotal = price.multiply(BigDecimal.valueOf(quantity));
    }
    public Long getId() { return id; } public Product getProduct() { return product; } public String getProductName() { return productName; }
    public int getQuantity() { return quantity; } public BigDecimal getPrice() { return price; } public BigDecimal getSubtotal() { return subtotal; }
}