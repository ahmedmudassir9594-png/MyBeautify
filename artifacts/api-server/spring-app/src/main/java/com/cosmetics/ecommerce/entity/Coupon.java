package com.cosmetics.ecommerce.entity;

import com.cosmetics.ecommerce.enums.DiscountType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons", indexes = @Index(name = "idx_coupon_code", columnList = "code", unique = true))
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(nullable = false, unique = true, length = 40) private String code;
    @Column(length = 500) private String description;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private DiscountType discountType;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal discountValue;
    @Column(nullable = false, precision = 12, scale = 2) private BigDecimal minimumOrderAmount = BigDecimal.ZERO;
    @Column(precision = 12, scale = 2) private BigDecimal maximumDiscount;
    @Column(nullable = false) private Instant startDate;
    @Column(nullable = false) private Instant expiryDate;
    private Integer usageLimit;
    @Column(nullable = false) private int usedCount;
    @Column(nullable = false) private boolean active = true;
    protected Coupon() {}
    public Coupon(String code, DiscountType discountType, BigDecimal discountValue, Instant startDate, Instant expiryDate) {
        this.code = code; this.discountType = discountType; this.discountValue = discountValue;
        this.startDate = startDate; this.expiryDate = expiryDate;
    }
    public Long getId() { return id; } public String getCode() { return code; } public String getDescription() { return description; }
    public DiscountType getDiscountType() { return discountType; } public BigDecimal getDiscountValue() { return discountValue; }
    public BigDecimal getMinimumOrderAmount() { return minimumOrderAmount; } public BigDecimal getMaximumDiscount() { return maximumDiscount; }
    public Instant getStartDate() { return startDate; } public Instant getExpiryDate() { return expiryDate; }
    public Integer getUsageLimit() { return usageLimit; } public int getUsedCount() { return usedCount; } public boolean isActive() { return active; }
    public void setDescription(String v) { description = v; } public void setMinimumOrderAmount(BigDecimal v) { minimumOrderAmount = v; }
    public void setMaximumDiscount(BigDecimal v) { maximumDiscount = v; } public void setUsageLimit(Integer v) { usageLimit = v; }
    public void setActive(boolean v) { active = v; } public void incrementUsedCount() { usedCount++; }
    public boolean availableAt(Instant now) { return active && !now.isBefore(startDate) && now.isBefore(expiryDate) && (usageLimit == null || usedCount < usageLimit); }
}