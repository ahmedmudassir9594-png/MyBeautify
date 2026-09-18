package com.cosmetics.ecommerce.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "product_variants", indexes = @Index(name = "idx_variant_sku", columnList = "sku", unique = true))
public class ProductVariant {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @ManyToOne(fetch = FetchType.LAZY, optional = false) private Product product;
    @Column(nullable = false) private String variantName;
    @Column(nullable = false, unique = true) private String sku;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal price;
    @Column(nullable = false) private int stockQuantity;
    private String imageUrl;
    @Column(nullable = false) private boolean active = true;
    protected ProductVariant() {}
}