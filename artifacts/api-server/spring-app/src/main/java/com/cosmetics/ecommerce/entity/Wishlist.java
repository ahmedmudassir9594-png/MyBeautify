package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "wishlists", uniqueConstraints = @UniqueConstraint(name = "uk_wishlist_user_product", columnNames = {"user_id", "product_id"}))
public class Wishlist {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @Column(nullable = false, updatable = false) private Instant createdAt = Instant.now();
    protected Wishlist() {}
    public Wishlist(User user, Product product) { this.user = user; this.product = product; }
    public Long getId() { return id; } public Product getProduct() { return product; } public Instant getCreatedAt() { return createdAt; }
}