package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import java.time.Instant;

@Entity
@Table(name = "categories", indexes = @Index(name = "idx_category_name", columnList = "name", unique = true))
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false, unique = true, length = 120)
    private String name;
    @Column(length = 1000)
    private String description;
    private String imageUrl;
    @Column(nullable = false)
    private boolean active = true;
    @CreationTimestamp @Column(nullable = false, updatable = false)
    private Instant createdAt;
    @UpdateTimestamp private Instant updatedAt;
    protected Category() {}
    public Category(String name, String description) { this.name = name; this.description = description; }
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public boolean isActive() { return active; }
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    public void setActive(boolean active) { this.active = active; }
}