package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "reviews", uniqueConstraints = @UniqueConstraint(name = "uk_review_user_product_order", columnNames = {"user_id", "product_id", "order_id"}))
public class Review {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private User user;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Order order;
    @Column(nullable = false) private int rating;
    @Column(nullable = false, length = 2000) private String comment;
    @Column(nullable = false) private boolean approved;
    @CreationTimestamp @Column(nullable = false, updatable = false) private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
    protected Review() {}
    public Review(User user, Product product, Order order, int rating, String comment) {
        this.user = user; this.product = product; this.order = order; this.rating = rating; this.comment = comment;
    }
    public Long getId() { return id; } public User getUser() { return user; } public Product getProduct() { return product; }
    public Order getOrder() { return order; } public int getRating() { return rating; } public String getComment() { return comment; }
    public boolean isApproved() { return approved; } public Instant getCreatedAt() { return createdAt; } public Instant getUpdatedAt() { return updatedAt; }
    public void setRating(int v) { rating = v; } public void setComment(String v) { comment = v; } public void setApproved(boolean v) { approved = v; }
}