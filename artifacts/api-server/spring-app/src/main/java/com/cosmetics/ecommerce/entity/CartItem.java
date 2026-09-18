package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "cart_items", uniqueConstraints = @UniqueConstraint(name = "uk_cart_product", columnNames = {"cart_id", "product_id"}))
public class CartItem {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Cart cart;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @Column(nullable = false) private int quantity;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    protected CartItem() {}
    public CartItem(Cart cart, Product product, int quantity) {
        this.cart = cart; this.product = product; this.quantity = quantity; this.price = product.sellingPrice();
    }
    public Long getId() { return id; } public Cart getCart() { return cart; } public Product getProduct() { return product; }
    public int getQuantity() { return quantity; } public BigDecimal getPrice() { return price; }
    public void setQuantity(int quantity) { this.quantity = quantity; this.price = product.sellingPrice(); }
    public BigDecimal subtotal() { return price.multiply(BigDecimal.valueOf(quantity)); }
}