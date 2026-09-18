package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "product_images")
public class ProductImage {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @Column(nullable = false) private String imageUrl;
    @Column(nullable = false) private int displayOrder;
    protected ProductImage() {}
}