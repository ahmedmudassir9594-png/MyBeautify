package com.cosmetics.ecommerce.dto;

import com.cosmetics.ecommerce.entity.Coupon;
import com.cosmetics.ecommerce.enums.DiscountType;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

public final class CouponDtos {
    private CouponDtos() {}
    public record ApplyCouponRequest(@NotBlank String code, @NotNull @PositiveOrZero BigDecimal orderAmount) {}
    public record CouponRequest(@NotBlank String code, String description, @NotNull DiscountType discountType,
                                @NotNull @Positive BigDecimal discountValue, @PositiveOrZero BigDecimal minimumOrderAmount,
                                @Positive BigDecimal maximumDiscount, @NotNull Instant startDate, @NotNull Instant expiryDate,
                                @Positive Integer usageLimit, Boolean active) {}
    public record CouponResponse(Long id, String code, String description, DiscountType discountType, BigDecimal discountValue,
                                 BigDecimal minimumOrderAmount, BigDecimal maximumDiscount, Instant startDate, Instant expiryDate,
                                 Integer usageLimit, int usedCount, boolean active) {
        public static CouponResponse from(Coupon c) { return new CouponResponse(c.getId(), c.getCode(), c.getDescription(), c.getDiscountType(),
                c.getDiscountValue(), c.getMinimumOrderAmount(), c.getMaximumDiscount(), c.getStartDate(), c.getExpiryDate(),
                c.getUsageLimit(), c.getUsedCount(), c.isActive()); }
    }
}