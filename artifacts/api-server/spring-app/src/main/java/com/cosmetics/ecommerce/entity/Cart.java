package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "carts", indexes = @Index(name = "idx_cart_user", columnList = "user_id", unique = true))
public class Cart {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @OneToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @Column(nullable = false) private Instant createdAt = Instant.now();
    @Column(nullable = false) private Instant updatedAt = Instant.now();
    @OneToMany(mappedBy = "cart", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<CartItem> items = new ArrayList<>();
    protected Cart() {}
    public Cart(User user) { this.user = user; }
    public Long getId() { return id; } public User getUser() { return user; }
    public Instant getCreatedAt() { return createdAt; } public Instant getUpdatedAt() { return updatedAt; }
    public List<CartItem> getItems() { return items; }
    public void touch() { updatedAt = Instant.now(); }
}